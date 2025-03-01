package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectCampaignItem;
import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

public interface ProjectCampaignJdbcRepository extends ListCrudRepository<ProjectCampaignEntity, Long> {

    Page<ProjectCampaignEntity> findAllByProjectId(long projectId, Pageable pageable);

    Page<ProjectCampaignItem> findAllByProjectIdAndNameContainingIgnoreCase(long projectId, String name, Pageable pageable);

}
