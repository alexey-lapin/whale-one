package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.model.DeploymentRef;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentStatus;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import com.github.alexeylapin.whaleone.domain.model.Ref;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public interface EquipmentJdbcRepository extends ListCrudRepository<EquipmentEntity, Long> {

    @Query("""
            SELECT e.*,
                   et.name type_name,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM equipment e
                     JOIN equipment_type et on e.type_id = et.id
                     JOIN tbl_user u1 on e.created_by_id = u1.id
                     JOIN tbl_user u2 on e.last_updated_by_id = u2.id
            WHERE e.id = :id""")
    Optional<EquipmentProjection> findOneById(long id);

    @Query(value = """
            SELECT e.*,
                   et.name type_name,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM equipment e
                     JOIN equipment_type et on e.type_id = et.id
                     JOIN tbl_user u1 on e.created_by_id = u1.id
                     JOIN tbl_user u2 on e.last_updated_by_id = u2.id
            WHERE
                1 = 1
                AND (:name IS NULL OR e.name ILIKE '%' || :name || '%')
                AND (:typeId IS NULL OR e.type_id = :typeId)
                AND (:manufacturer IS NULL OR e.manufacturer ILIKE '%' || :manufacturer || '%')
                AND (:model IS NULL OR e.model ILIKE '%' || :model || '%')
            ORDER BY et.name, e.manufacturer, e.model, e.name
            LIMIT :size OFFSET :offset""",
            rowMapperClass = EquipmentListElementRowMapper.class)
    List<EquipmentListElement> findAllElements(long size,
                                               long offset,
                                               String name,
                                               Long typeId,
                                               String manufacturer,
                                               String model);

    List<EquipmentEntity> findAllByDeploymentId(long id);

    @Query("""
            SELECT e.id, e.name
            FROM equipment e
            WHERE e.id = :id
            """)
    Optional<EquipmentItem> findItemById(long id);

    @Query("""
            SELECT e.id, e.name
            FROM equipment e
            WHERE
                1 = 1
                AND e.type_id = :typeId
                AND e.active = TRUE
                AND e.assembly_id IS NULL
                AND (:name IS NULL OR e.name ILIKE '%' || :name || '%')
                AND (:includeAllocated = TRUE OR e.deployment_id IS NULL)
            """)
    List<EquipmentItem> findAllItems(long size,
                                     long offset,
                                     long typeId,
                                     @Nullable String name,
                                     boolean includeAllocated);

    @Modifying
    @Query("UPDATE equipment SET deployment_id = :deploymentId WHERE id = :id")
    void updateDeploymentId(long id, Long deploymentId);

    class EquipmentListElementRowMapper implements RowMapper<EquipmentListElement> {
        @Override
        public EquipmentListElement mapRow(ResultSet rs, int rowNum) throws SQLException {
            DeploymentRef deploymentRef;
            Long deploymentId = rs.getLong("deployment_id");
            if (rs.wasNull()) {
                deploymentRef = null;
            } else {
                String deploymentName = rs.getString("deployment_name");
                deploymentRef = new DeploymentRef(deploymentId, deploymentName);
            }
            Long assemblyId = rs.getLong("assembly_id");
            if (rs.wasNull()) {
                assemblyId = null;
            }
            return EquipmentListElement.builder()
                    .id(rs.getLong("id"))
                    .version(rs.getInt("version"))
                    .createdAt(rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()))
                    .createdBy(new UserRef(rs.getLong("created_by_id"), rs.getString("created_by_name")))
                    .lastUpdatedAt(rs.getTimestamp("last_updated_at").toInstant().atZone(ZoneId.systemDefault()))
                    .lastUpdatedBy(new UserRef(rs.getLong("last_updated_by_id"), rs.getString("last_updated_by_name")))
                    .active(rs.getBoolean("active"))
                    .status(EquipmentStatus.valueOf(rs.getString("status")))
                    .name(rs.getString("name"))
                    .type(new EquipmentTypeRef(rs.getLong("type_id"), rs.getString("type_name")))
                    .manufacturer(rs.getString("manufacturer"))
                    .model(rs.getString("model"))
                    .assemblyId(assemblyId)
                    .deployment(deploymentRef)
                    .build();
        }
    }

    @Getter
    @Setter
    class EquipmentProjection extends EquipmentEntity {

        private String createdByName;
        private String lastUpdatedByName;
        private String typeName;

    }

}
