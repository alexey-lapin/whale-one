package com.github.alexeylapin.whaleone.application.service.impl;

import com.github.alexeylapin.whaleone.application.service.EquipmentService;
import com.github.alexeylapin.whaleone.application.service.QuerySpecFactory;
import com.github.alexeylapin.whaleone.application.service.ex.ApplicationException;
import com.github.alexeylapin.whaleone.application.service.ex.NotFoundException;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public class DefaultEquipmentService implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final QuerySpecFactory querySpecFactory;

    public DefaultEquipmentService(@NotNull EquipmentRepository equipmentRepository,
                                   @NotNull QuerySpecFactory querySpecFactory) {
        this.equipmentRepository = equipmentRepository;
        this.querySpecFactory = querySpecFactory;
    }

    @Override
    public Page<EquipmentListElement> list(int page, int size, String filter) {
        QuerySpec querySpec = querySpecFactory.create(filter);
        return equipmentRepository.list(page, size, querySpec);
    }

    @Override
    public Equipment toggleActive(long id, UserRef user) {
        var equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Equipment.class, id));

        var activation = !equipment.active();

        if (equipment.assemblyId() != null) {
            throw new ApplicationException("Cannot activate/deactivate equipment that is part of an assembly");
        }

        if (equipment.assemblyParts() != null) {
            for (var assemblyPart : equipment.assemblyParts()) {
                var partEquipment = equipmentRepository.findById(assemblyPart.equipmentId())
                        .orElseThrow(() -> new NotFoundException(Equipment.class, assemblyPart.equipmentId()));
                var updatedPartEquipmentBuilder = partEquipment.toBuilder();
                if (activation) {
                    if (partEquipment.assemblyId() != null) {
                        throw new ApplicationException("Cannot activate equipment with assembly parts " +
                                                       "that are already assigned to another assembly");
                    }
                    if (!partEquipment.active()) {
                        throw new ApplicationException("Cannot activate equipment with assembly parts " +
                                                       "that are not active");
                    }
                    updatedPartEquipmentBuilder.assemblyId(id);
                } else {
                    if (partEquipment.assemblyId() != id) {
                        throw new ApplicationException("Cannot deactivate equipment with assembly parts " +
                                                       "that are not assigned to this assembly");
                    }
                    updatedPartEquipmentBuilder.assemblyId(null);
                }
                equipmentRepository.save(updatedPartEquipmentBuilder.build());
            }
        }

        var updatedEquipment = equipment.toBuilder()
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(user)
                .active(activation)
                .build();
        return equipmentRepository.save(updatedEquipment);
    }

}
