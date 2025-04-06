package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EquipmentApi {

    private final EquipmentRepository equipmentRepository;

    @PostMapping("/equipment")
    public Equipment create(@RequestBody Equipment equipment,
                            @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        equipment = equipment.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
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
        var equipment = equipmentRepository.findById(id).orElseThrow();
        var activation = !equipment.active();

        if (equipment.assemblyId() != null) {
            throw new IllegalStateException("Cannot activate/deactivate equipment that is part of an assembly");
        }

        if (equipment.assemblyParts() != null) {
            for (var assemblyPart : equipment.assemblyParts()) {
                var partEquipment = equipmentRepository.findById(assemblyPart.equipmentId()).orElseThrow();
                Equipment updatedPartEquipment;
                if (activation) {
                    if (partEquipment.assemblyId() != null) {
                        throw new IllegalStateException("Cannot activate equipment with assembly parts that are already assigned to another assembly");
                    }
                    if (!partEquipment.active()) {
                        throw new IllegalStateException("Cannot activate equipment with assembly parts that are not active");
                    }
                    updatedPartEquipment = partEquipment.toBuilder().assemblyId(id).build();
                } else {
                    if (partEquipment.assemblyId() != id) {
                        throw new IllegalStateException("Cannot deactivate equipment with assembly parts that are not assigned to this assembly");
                    }
                    updatedPartEquipment = partEquipment.toBuilder().assemblyId(null).build();
                }
                equipmentRepository.save(updatedPartEquipment);
            }
        }

        var updatedEquipment = equipment.toBuilder()
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .active(activation)
                .build();
        return equipmentRepository.save(updatedEquipment);
    }

    @GetMapping("/equipment/{id}")
    public Equipment get(@PathVariable long id) {
        return equipmentRepository.findById(id).orElseThrow();
    }

    @GetMapping("/equipment")
    public PageDto<EquipmentListElement> getAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam Optional<String> name,
                                                @RequestParam Optional<Long> typeId,
                                                @RequestParam Optional<String> manufacturer,
                                                @RequestParam Optional<String> model) {
        var aPage = equipmentRepository.findAllElements(page,
                size,
                name.orElse(null),
                typeId.orElse(null),
                manufacturer.orElse(null),
                model.orElse(null));
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
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

}
