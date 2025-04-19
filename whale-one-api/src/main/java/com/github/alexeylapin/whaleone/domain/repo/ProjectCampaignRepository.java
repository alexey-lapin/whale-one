package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaign;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaignItem;

import java.util.List;
import java.util.Optional;

public interface ProjectCampaignRepository {

    ProjectCampaign save(ProjectCampaign projectCampaign);

    Optional<ProjectCampaign> findById(long id);

    Page<ProjectCampaign> findAll(long projectId);

    List<ProjectCampaignItem> findAllItems(long projectId, String nameQuery);

    void deleteById(long id);

}
