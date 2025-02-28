package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeploymentJdbcRepository extends ListCrudRepository<DeploymentEntity, Long> {

    @Query("""
            SELECT d.*,
                   u.username created_by_name,
                   p.name project_name,
                   ps.name project_site_name
            FROM deployment d
                     JOIN tbl_user u on d.created_by_id = u.id
                     JOIN project p on d.project_id = p.id
                     JOIN project_site ps on d.project_site_id = ps.id
            WHERE d.id = :id""")
    Optional<DeploymentProjection> findOneById(long id);

    @Query("""
            SELECT d.*,
                   u.username created_by_name,
                   p.name project_name,
                   ps.name project_site_name
            FROM deployment d
                     JOIN tbl_user u ON d.created_by_id = u.id
                     JOIN project p on d.project_id = p.id
                     JOIN project_site ps on d.project_site_id = ps.id
            ORDER BY d.id DESC
            LIMIT :size OFFSET :offset""")
    List<DeploymentProjection> findAll(long size, long offset);

    @Getter
    @Setter
    class DeploymentProjection extends DeploymentEntity {

        private String createdByName;
        private String projectName;
        private String projectSiteName;

    }

}
