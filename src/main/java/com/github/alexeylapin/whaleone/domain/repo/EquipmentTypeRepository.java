package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeRepository {

    List<EquipmentType> findAll();

    Optional<EquipmentType> findById(int id);

    EquipmentType save(EquipmentType equipmentType);

}
