package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Deployment;

import java.util.Optional;

public interface DeploymentRepository {

    Deployment save(Deployment deployment);

    Optional<Deployment> findById(long id);

    Page<Deployment> findAll(int page,
                             int size,
                             String name,
                             Long projectId,
                             Long projectSiteId,
                             String status);

}
