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
                   u1.username created_by_name,
                   u2.username last_updated_by_name,
                   p.name project_name,
                   ps.name project_site_name
            FROM deployment d
                     JOIN tbl_user u1 on d.created_by_id = u1.id
                     JOIN tbl_user u2 on d.last_updated_by_id = u2.id
                     JOIN project p on d.project_id = p.id
                     JOIN project_site ps on d.project_site_id = ps.id
            WHERE d.id = :id""")
    Optional<DeploymentProjection> findOneById(long id);

    @Query("""
            SELECT d.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name,
                   p.name project_name,
                   ps.name project_site_name
            FROM deployment d
                     JOIN tbl_user u1 on d.created_by_id = u1.id
                     JOIN tbl_user u2 on d.last_updated_by_id = u2.id
                     JOIN project p on d.project_id = p.id
                     JOIN project_site ps on d.project_site_id = ps.id
            WHERE
                1 = 1
                AND (:name IS NULL OR d.name ILIKE '%' || :name || '%')
                AND (:projectId IS NULL OR d.project_id = :projectId)
                AND (:projectSiteId IS NULL OR d.project_site_id = :projectSiteId)
                AND (:status IS NULL OR d.status = :status)
            ORDER BY d.id DESC
            LIMIT :size OFFSET :offset""")
    List<DeploymentProjection> findAll(int size,
                                       long offset,
                                       String name,
                                       Long projectId,
                                       Long projectSiteId,
                                       String status);

    @Getter
    @Setter
    class DeploymentProjection extends DeploymentEntity {

        private String createdByName;
        private String lastUpdatedByName;
        private String projectName;
        private String projectSiteName;

    }

}
