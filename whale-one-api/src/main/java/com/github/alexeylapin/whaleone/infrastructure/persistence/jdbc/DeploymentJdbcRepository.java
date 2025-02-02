package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeploymentJdbcRepository extends ListCrudRepository<DeploymentEntity, Long> {

    @Query("""
            SELECT d.*,
                   u.username created_by_name
            FROM deployment d
                     JOIN tbl_user u on d.created_by_id = u.id
            WHERE d.id = :id""")
    Optional<DeploymentWithUserNameEntity> findOneById(long id);

    @Query("""
            SELECT d.*,
                   u.username created_by_name
            FROM deployment d
                     JOIN tbl_user u ON d.created_by_id = u.id
            ORDER BY d.id DESC
            LIMIT :size OFFSET :offset""")
    List<DeploymentWithUserNameEntity> findAll(long size, long offset);

}
