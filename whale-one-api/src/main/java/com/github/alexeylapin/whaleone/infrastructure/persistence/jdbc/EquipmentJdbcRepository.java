package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeRef;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public interface EquipmentJdbcRepository extends ListCrudRepository<EquipmentEntity, Long> {

    @Query("""
            SELECT e.*,
                   u.username created_by_name
            FROM equipment e
                     JOIN tbl_user u on e.created_by_id = u.id
            WHERE e.id = :id""")
    Optional<EquipmentWithUserNameEntity> findOneById(long id);

    @Query("""
            SELECT e.*,
                   u.username created_by_name
            FROM equipment e
                     JOIN tbl_user u on e.created_by_id = u.id
            ORDER BY e.id DESC
            LIMIT :size OFFSET :offset""")
    List<EquipmentWithUserNameEntity> findAll(long size, long offset);

    @Query(value = """
            SELECT e.*,
                   et.name type_name,
                   et.id type_id,
                   u.username created_by_name
            FROM equipment e
                     JOIN tbl_user u on e.created_by_id = u.id
                     JOIN equipment_type et on e.type = et.id
            WHERE
                1 = 1
                AND (:name IS NULL OR e.name ILIKE '%' || :name || '%')
                AND (:typeId IS NULL OR e.type = :typeId)
            ORDER BY e.id DESC
            LIMIT :size OFFSET :offset""",
            rowMapperClass = EquipmentListElementRowMapper.class)
    List<EquipmentListElement> findAllElements(long size, long offset, String name, Long typeId);

    List<EquipmentEntity> findAllByDeploymentId(long id);

    class EquipmentListElementRowMapper implements RowMapper<EquipmentListElement> {

        @Override
        public EquipmentListElement mapRow(ResultSet rs, int rowNum) throws SQLException {
            return EquipmentListElement.builder()
                    .id(rs.getLong("id"))
                    .version(rs.getInt("version"))
                    .createdAt(rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()))
                    .createdById(rs.getLong("created_by_id"))
                    .createdBy(rs.getString("created_by_name"))
                    .name(rs.getString("name"))
                    .type(new EquipmentTypeRef(rs.getLong("type_id"), rs.getString("type_name")))
                    .deploymentId(rs.getLong("deployment_id"))
                    .build();
        }
    }

}
