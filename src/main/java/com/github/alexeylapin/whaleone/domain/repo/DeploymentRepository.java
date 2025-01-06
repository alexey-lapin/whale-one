package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Deployment;

import java.util.List;
import java.util.Optional;

public interface DeploymentRepository {

    List<Deployment> findAll();

    Optional<Deployment> findById(long id);

    Deployment save(Deployment deployment);

}
