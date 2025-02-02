package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeRepository {

    EquipmentType save(EquipmentType equipmentType);

    Optional<EquipmentType> findById(long id);

    Page<EquipmentType> findAll(int page, int size);

}
