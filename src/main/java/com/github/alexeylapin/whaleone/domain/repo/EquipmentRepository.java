package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(long id);

    Page<Equipment> findAll(int page, int size);

    List<Equipment> findAllByDeploymentId(long id);

}
