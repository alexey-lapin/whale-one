package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Project;
import com.github.alexeylapin.whaleone.domain.model.ProjectItem;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(long id);

    Page<Project> findAll(int page, int size);

    List<ProjectItem> findAllItems(String nameQuery);

}
