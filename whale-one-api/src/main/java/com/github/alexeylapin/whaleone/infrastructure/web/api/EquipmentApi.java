package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EquipmentApi {

    private final EquipmentRepository equipmentRepository;

    @PostMapping("/equipment")
    public Equipment create(@RequestBody Equipment equipment,
                            @AuthenticationPrincipal IdUser user) {
        equipment = equipment.toBuilder()
                .id(0)
                .version(0)
                .createdAt(ZonedDateTime.now())
                .createdBy(new UserRef(user.getId(), user.getName()))
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
        equipment = equipment.toBuilder()
                .id(id)
                .build();
        return equipmentRepository.save(equipment);
    }

    @GetMapping("/equipment/{id}")
    public Equipment get(@PathVariable long id) {
        return equipmentRepository.findById(id).orElseThrow();
    }

    @GetMapping("/equipment")
    public PageDto<EquipmentListElement> getAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam Optional<String> name,
                                                @RequestParam Optional<Long> typeId) {
        var aPage = equipmentRepository.findAllElements(page, size, name.orElse(null), typeId.orElse(null));
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

}
