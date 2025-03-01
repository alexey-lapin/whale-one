package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;

import java.util.Optional;

public interface EquipmentTypeDeploymentAttributeRepository {

    EquipmentTypeAttribute save(EquipmentTypeAttribute equipmentTypeAttribute);

    Optional<EquipmentTypeAttribute> findById(long id);

    Page<EquipmentTypeAttribute> findAll(long equipmentTypeId);

//    List<ProjectSiteItem> findAllItems(long projectRef, String nameQuery);

    void deleteById(long id);

}
