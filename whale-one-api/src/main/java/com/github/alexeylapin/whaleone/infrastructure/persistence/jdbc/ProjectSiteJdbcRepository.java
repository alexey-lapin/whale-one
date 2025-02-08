package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface ProjectSiteJdbcRepository extends ListCrudRepository<ProjectSiteEntity, Long> {

    Page<ProjectSiteEntity> findAllByProjectId(long projectId, Pageable pageable);

    Page<ProjectSiteItem> findAllByProjectIdAndNameContainingIgnoreCase(long projectId, String name, Pageable pageable);

}
