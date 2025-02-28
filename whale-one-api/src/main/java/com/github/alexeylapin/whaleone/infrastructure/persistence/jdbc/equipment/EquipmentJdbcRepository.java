package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import lombok.Getter;
import lombok.Setter;
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
                   u.username created_by_name
            FROM equipment e
                     JOIN equipment_type et on e.type_id = et.id
                     JOIN tbl_user u on e.created_by_id = u.id
            WHERE e.id = :id""")
    Optional<EquipmentProjection> findOneById(long id);

    @Query(value = """
            SELECT e.*,
                   et.name type_name,
                   u.username created_by_name
            FROM equipment e
                     JOIN equipment_type et on e.type_id = et.id
                     JOIN tbl_user u on e.created_by_id = u.id
            WHERE
                1 = 1
                AND (:name IS NULL OR e.name ILIKE '%' || :name || '%')
                AND (:typeId IS NULL OR e.type_id = :typeId)
            ORDER BY e.id DESC
            LIMIT :size OFFSET :offset""",
            rowMapperClass = EquipmentListElementRowMapper.class)
    List<EquipmentListElement> findAllElements(long size, long offset, String name, Long typeId);

    List<EquipmentEntity> findAllByDeploymentId(long id);

    @Query("""
            SELECT e.id, e.name
            FROM equipment e
            WHERE
                1 = 1
                AND e.type_id = :typeId
                AND e.active = TRUE
                AND (:name IS NULL OR e.name ILIKE '%' || :name || '%')
                AND (:includeAllocated = TRUE OR e.deployment_id IS NULL)
            """)
    List<EquipmentItem> findAllItems(long size,
                                     long offset,
                                     long typeId,
                                     @Nullable String name,
                                     boolean includeAllocated);

    class EquipmentListElementRowMapper implements RowMapper<EquipmentListElement> {
        @Override
        public EquipmentListElement mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long deploymentId = rs.getLong("deployment_id");
            if (rs.wasNull()) {
                deploymentId = null;
            }
            return EquipmentListElement.builder()
                    .id(rs.getLong("id"))
                    .version(rs.getInt("version"))
                    .createdAt(rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()))
                    .createdBy(new UserRef(rs.getLong("created_by_id"), rs.getString("created_by_name")))
                    .active(rs.getBoolean("active"))
                    .name(rs.getString("name"))
                    .type(new EquipmentTypeRef(rs.getLong("type_id"), rs.getString("type_name")))
                    .deploymentId(deploymentId)
                    .build();
        }
    }

    @Getter
    @Setter
    class EquipmentProjection extends EquipmentEntity {

        private String createdByName;
        private String typeName;

    }

}
