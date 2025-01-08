package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface DeploymentJdbcRepository extends Repository<JdbcDeploymentEntity, Long> {

    @Query("""
            SELECT d.id,
                   d.name,
                   d.description,
                   d.status,
                   d.job_id,
                   d.site_id,
                   d.created_at,
                   d.created_by_id,
                   u.username created_by_name
            FROM deployment d
                     JOIN tbl_user u on d.created_by_id = u.id
            WHERE d.id = :id""")
    Optional<JdbcDeploymentWithUserNameEntity> findById(Long id);

    @Query("""
            SELECT d.id,
                   d.name,
                   d.description,
                   d.status,
                   d.job_id,
                   d.site_id,
                   d.created_at,
                   d.created_by_id,
                   u.username created_by_name
            FROM deployment d
                     JOIN tbl_user u ON d.created_by_id = u.id""")
    List<JdbcDeploymentWithUserNameEntity> findAll();

    JdbcDeploymentEntity save(JdbcDeploymentEntity entity);

}
