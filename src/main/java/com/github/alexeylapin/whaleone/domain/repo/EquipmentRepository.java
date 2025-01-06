package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Equipment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EquipmentRepository {

    List<Equipment> findAll();

    List<Equipment> findAllByDeploymentId(long id);

    Optional<Equipment> findById(long id);

    List<Equipment> findAllUnassigned();

    Equipment save(Equipment equipment);
}
