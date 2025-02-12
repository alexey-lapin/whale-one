package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(long id);

    @Deprecated
    Page<Equipment> findAll(int page, int size);

    Page<EquipmentListElement> findAllElements(int page, int size, String name, Long typeId);

    List<Equipment> findAllByDeploymentId(long id);

}
