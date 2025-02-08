package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface ProjectJdbcRepository extends ListCrudRepository<ProjectEntity, Long> {

    @Query("""
            SELECT p.*,
                   u.username created_by_name
            FROM project p
                     JOIN tbl_user u on p.created_by_id = u.id
            WHERE p.id = :id""")
    Optional<ProjectWithUserNameEntity> findOneById(long id);

    @Query("""
            SELECT p.*,
                   u.username created_by_name
            FROM project p
                     JOIN tbl_user u ON p.created_by_id = u.id
            ORDER BY p.id DESC
            LIMIT :size OFFSET :offset""")
    List<ProjectWithUserNameEntity> findAll(long size, long offset);

    Page<ProjectItem> findAllByNameContainingIgnoreCase(@Nullable String name, Pageable pageable);

    @Getter
    @Setter
    class ProjectWithUserNameEntity extends ProjectEntity {

        private String createdByName;

    }

}
