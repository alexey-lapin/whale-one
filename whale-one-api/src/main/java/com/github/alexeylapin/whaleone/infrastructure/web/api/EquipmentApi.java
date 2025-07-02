package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.application.service.EquipmentService;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentAssemblyPart;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EquipmentApi {

    private static final DateTimeFormatter FORMATTER_EXPORT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final EquipmentRepository equipmentRepository;
    private final EquipmentService equipmentService;

    @PostMapping("/equipment")
    public Equipment create(@RequestBody Equipment equipment,
                            @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        String assemblyDescriptor = null;
        if (equipment.assemblyParts() != null) {
            assemblyDescriptor = equipment.assemblyParts().stream()
                    .map(EquipmentAssemblyPart::equipmentId)
                    .sorted()
                    .map(Object::toString)
                    .collect(Collectors.joining("-"));
        }
        equipment = equipment.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .assemblyDescriptor(assemblyDescriptor)
                .build();
        return equipmentRepository.save(equipment);
    }

    @PutMapping("/equipment/{id}")
    public Equipment update(@PathVariable long id,
                            @RequestBody Equipment equipment,
                            @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing equipment expected");
        Assert.isTrue(equipment.version() > 0,
                "equipment.version must be greater than 0 - existing equipment expected");
        Assert.isTrue(id == equipment.id(),
                "id must match");
        var existingEquipment = equipmentRepository.findById(id).orElseThrow();
        var updatedEquipment = equipment.toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .active(existingEquipment.active())
                .build();
        return equipmentRepository.save(updatedEquipment);
    }

    @Transactional
    @PutMapping("/equipment/{id}/active")
    public Equipment toggleActive(@PathVariable long id,
                                  @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing equipment expected");
        return equipmentService.toggleActive(id, null, new UserRef(user.getId(), user.getName()));
    }

    @GetMapping("/equipment/{id}")
    public Equipment get(@PathVariable long id) {
        return equipmentRepository.findById(id).orElseThrow();
    }

    @GetMapping("/equipment/descriptor/{descriptor}")
    public EquipmentItem findByDescriptor(@PathVariable String descriptor) {
        return equipmentRepository.findByAssemblyDescriptor(descriptor)
                .orElseThrow(() -> new NotFoundException("Failed to find %s with assemblyDescriptor=%s"
                        .formatted(Equipment.class.getName(), descriptor)));
    }

    @GetMapping("/equipment")
    public PageDto<EquipmentListElement> list(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam Optional<String> filters) {
        var aPage = equipmentService.list(page, size, filters.orElse(null));
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    @GetMapping("/equipment/search")
    public PageDto<Equipment> search(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam Optional<String> filters) {
        var aPage = equipmentService.search(page, size, filters.orElse(null));
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    @GetMapping("/equipment/export")
    public void export(@RequestParam Optional<String> filters, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename("equipment-export-%s.csv".formatted(FORMATTER_EXPORT.format(LocalDateTime.now())))
                .build()
                .toString());
        equipmentService.export(filters.orElse(null), response.getOutputStream());
    }

    @GetMapping("/equipment/items")
    public List<EquipmentItem> getAllItems(@RequestParam Long typeId,
                                           @RequestParam Optional<String> name,
                                           @RequestParam Optional<Long> deploymentId) {
        return equipmentRepository.findAllItems(typeId, name.orElse(null), false);
    }

    @GetMapping("/equipment/items/{id}")
    public EquipmentItem getItem(@PathVariable long id) {
        return equipmentRepository.findItemById(id).orElseThrow();
    }

    @DeleteMapping("/equipment/{id}")
    public void delete(@PathVariable long id) {
        equipmentRepository.deleteById(id);
    }

}
