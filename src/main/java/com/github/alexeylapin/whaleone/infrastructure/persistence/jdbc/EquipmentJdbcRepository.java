package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.List;

public interface EquipmentJdbcRepository extends ListCrudRepository<JdbcEquipmentEntity, Long> {

    List<JdbcEquipmentEntity> findAllByDeploymentId(long id);

    List<JdbcEquipmentEntity> findAllByDeploymentIdIsNull();

}
