package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.ProjectSite;
import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;

import java.util.List;
import java.util.Optional;

public interface ProjectSiteRepository {

    ProjectSite save(ProjectSite projectSite);

    Optional<ProjectSite> findById(long id);

    Page<ProjectSite> findAll(long projectId);

    List<ProjectSiteItem> findAllItems(long projectId, String nameQuery);

    void deleteById(long id);

}
