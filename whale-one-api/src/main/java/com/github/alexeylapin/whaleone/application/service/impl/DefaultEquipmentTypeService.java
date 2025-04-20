package com.github.alexeylapin.whaleone.application.service.impl;

import com.github.alexeylapin.whaleone.application.service.EquipmentTypeService;
import com.github.alexeylapin.whaleone.application.service.QuerySpecFactory;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;

public class DefaultEquipmentTypeService implements EquipmentTypeService {

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final QuerySpecFactory querySpecFactory;

    public DefaultEquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository,
                                       QuerySpecFactory querySpecFactory) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.querySpecFactory = querySpecFactory;
    }

    @Override
    public Page<EquipmentType> list(int page, int size, String filter) {
        QuerySpec querySpec = querySpecFactory.create(filter);
        return equipmentTypeRepository.list(page, size, querySpec);
    }

}
