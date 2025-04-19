package com.github.alexeylapin.whaleone.application.service.impl;

import com.github.alexeylapin.whaleone.application.service.EquipmentService;
import com.github.alexeylapin.whaleone.application.service.QuerySpecFactory;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import jakarta.validation.constraints.NotNull;

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

}
