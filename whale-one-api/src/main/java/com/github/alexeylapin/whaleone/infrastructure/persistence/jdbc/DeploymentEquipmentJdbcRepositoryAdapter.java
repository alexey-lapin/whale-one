package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class DeploymentEquipmentJdbcRepositoryAdapter implements DeploymentEquipmentRepository {

    private static final String SQL_FIND_ALL_ELEMENTS_BY_DEPLOYMENT_ID = """
            WITH RECURSIVE equipment_with_parts AS (
                SELECT e.id,
                       e.name,
                       et.id   AS type_id,
                       et.name AS type_name,
                       e.assembly_id,
                       0       AS level
                FROM whale_one.equipment e
                         JOIN
                     whale_one.equipment_type et ON e.type_id = et.id
                         JOIN
                     whale_one.deployment_equipment de ON e.id = de.equipment_id
                WHERE de.deployment_id = ?
            
                UNION ALL
            
                SELECT e.id,
                       e.name,
                       et.id   AS type_id,
                       et.name AS type_name,
                       e.assembly_id,
                       ewp.level + 1
                FROM whale_one.equipment e
                         JOIN
                     whale_one.equipment_type et ON e.type_id = et.id
                         JOIN
                     equipment_with_parts ewp ON e.assembly_id = ewp.id)
            SELECT id,
                   name,
                   type_id,
                   type_name,
                   assembly_id,
                   level
            FROM equipment_with_parts
            ORDER BY level,
                     id
            """.replace(System.lineSeparator(), " ");

    private static final EquipmentElementExtractor EQUIPMENT_ELEMENT_EXTRACTOR = new EquipmentElementExtractor();

    private final JdbcClient jdbcClient;
    private final DeploymentEquipmentJdbcRepository repository;

    @Override
    public List<DeploymentEquipmentElement> findAllElementsByDeploymentId(long deploymentId) {
        return jdbcClient.sql(SQL_FIND_ALL_ELEMENTS_BY_DEPLOYMENT_ID)
                .param(deploymentId)
                .query(EQUIPMENT_ELEMENT_EXTRACTOR);
    }

    @Override
    public void save(DeploymentEquipment deploymentEquipment) {
        repository.save(deploymentEquipment.deploymentId(), deploymentEquipment.equipmentId());
    }

    @Override
    public void delete(long deploymentId, long equipmentId) {
        repository.delete(deploymentId, equipmentId);
    }

    private static class EquipmentElementExtractor implements ResultSetExtractor<List<DeploymentEquipmentElement>> {

        @Override
        public List<DeploymentEquipmentElement> extractData(ResultSet rs) throws SQLException, DataAccessException {

            var nodeMap = new HashMap<Long, DeploymentEquipmentElement>();
            var rootNodes = new ArrayList<DeploymentEquipmentElement>();

            while (rs.next()) {
                var id = rs.getLong("id");
                var name = rs.getString("name");
                Long assemblyId = rs.getLong("assembly_id");
                if (rs.wasNull()) {
                    assemblyId = null;
                }

                var equipmentTypeRef = new EquipmentTypeRef(
                        rs.getLong("type_id"),
                        rs.getString("type_name")
                );

                var element = new DeploymentEquipmentElement(
                        id,
                        name,
                        assemblyId,
                        equipmentTypeRef,
                        new ArrayList<>()
                );

                nodeMap.put(id, element);

                if (assemblyId == null) {
                    rootNodes.add(element);
                } else {
                    DeploymentEquipmentElement parent = nodeMap.get(assemblyId);
                    if (parent != null) {
                        parent.assemblyParts().add(element);
                    }
                }
            }

            return rootNodes;
        }

    }

}
