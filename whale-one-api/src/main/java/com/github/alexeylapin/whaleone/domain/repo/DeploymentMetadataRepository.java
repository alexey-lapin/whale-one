package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata;

import java.util.Optional;

public interface DeploymentMetadataRepository {

    DeploymentMetadata save(DeploymentMetadata deploymentMetadata);

    Page<DeploymentMetadata> findAll(int page, int size);

    Optional<DeploymentMetadata> findById(long id);

}
