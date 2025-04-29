package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

public interface DeploymentEquipmentJdbcRepository extends Repository<DeploymentEquipment, DeploymentEquipment> {

    @Modifying
    @Query("INSERT INTO deployment_equipment (deployment_id, equipment_id) VALUES (:deploymentId, :equipmentId)")
    void save(long deploymentId, long equipmentId);

    @Modifying
    @Query("DELETE FROM deployment_equipment WHERE deployment_id = :deploymentId AND equipment_id = :equipmentId")
    void delete(long deploymentId, long equipmentId);

}
