package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DeploymentEquipmentJdbcRepository extends Repository<DeploymentEquipment, DeploymentEquipment> {

    @Query(value = """
            SELECT de.deployment_id,
                   et.id   as equipment_type_id,
                   et.name as equipment_type_name,
                   e.id    as equipment_id,
                   e.name  as equipment_name
            FROM deployment_equipment de
                     JOIN equipment e ON de.equipment_id = e.id
                     JOIN equipment_type et ON e.type_id = et.id
            WHERE de.deployment_id = :deploymentId""",
            rowMapperClass = DeploymentEquipmentItemRowMapper.class)
    List<DeploymentEquipmentItem> findAllByDeploymentId(long deploymentId);

    @Modifying
    @Query("INSERT INTO deployment_equipment (deployment_id, equipment_id) VALUES (:deploymentId, :equipmentId)")
    void save(long deploymentId, long equipmentId);

    @Modifying
    @Query("DELETE FROM deployment_equipment WHERE deployment_id = :deploymentId AND equipment_id = :equipmentId")
    void delete(long deploymentId, long equipmentId);

    class DeploymentEquipmentItemRowMapper implements RowMapper<DeploymentEquipmentItem> {

        @Override
        public DeploymentEquipmentItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return DeploymentEquipmentItem.builder()
                    .deploymentId(rs.getLong("deployment_id"))
                    .equipmentTypeRef(new EquipmentTypeRef(
                            rs.getLong("equipment_type_id"),
                            rs.getString("equipment_type_name")))
                    .equipmentRef(new EquipmentItem(
                            rs.getLong("equipment_id"),
                            rs.getString("equipment_name")))
                    .build();
        }

    }

}
