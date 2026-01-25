package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectItem;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJdbcRepository extends ListCrudRepository<ProjectEntity, Long> {

    @Query("""
            SELECT p.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM project p
                     JOIN tbl_user u1 on p.created_by_id = u1.id
                     JOIN tbl_user u2 on p.last_updated_by_id = u2.id
            WHERE p.id = :id""")
    Optional<ProjectProjection> findOneById(long id);

    @Query("""
            SELECT p.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM project p
                     JOIN tbl_user u1 on p.created_by_id = u1.id
                     JOIN tbl_user u2 on p.last_updated_by_id = u2.id
            ORDER BY p.id DESC
            LIMIT :size OFFSET :offset""")
    List<ProjectProjection> findAll(long size, long offset);

    Page<ProjectItem> findAllByNameContainingIgnoreCase(@Nullable String name, Pageable pageable);

    @Getter
    @Setter
    class ProjectProjection extends ProjectEntity {

        private String createdByName;
        private String lastUpdatedByName;

    }

}
