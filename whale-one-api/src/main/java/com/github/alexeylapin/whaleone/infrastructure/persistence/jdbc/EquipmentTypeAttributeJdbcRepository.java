package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface EquipmentTypeAttributeJdbcRepository extends ListCrudRepository<EquipmentTypeAttributeEntity, Long> {

    Page<EquipmentTypeAttributeEntity> findByEquipmentTypeId(long equipmentTypeId, Pageable pageable);

}
