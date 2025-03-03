package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface EquipmentTypeEquipmentAttributeJdbcRepository
        extends ListCrudRepository<EquipmentTypeEquipmentAttributeEntity, Long> {

    Page<EquipmentTypeEquipmentAttributeEntity> findByEquipmentTypeId(long equipmentTypeId, Pageable pageable);

}
