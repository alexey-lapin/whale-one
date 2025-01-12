package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

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

    List<EquipmentEntity> findAllByDeploymentId(long id);

}
