package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface EquipmentTypeDeploymentAttributeJdbcRepository
        extends ListCrudRepository<EquipmentTypeDeploymentAttributeEntity, Long> {

    Page<EquipmentTypeDeploymentAttributeEntity> findByEquipmentTypeId(long equipmentTypeId, Pageable pageable);

}
