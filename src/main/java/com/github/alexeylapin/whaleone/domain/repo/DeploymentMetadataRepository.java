package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata;

import java.util.Optional;

public interface DeploymentMetadataRepository {

    Optional<DeploymentMetadata> findById(long id);

    DeploymentMetadata save(DeploymentMetadata deploymentMetadata);

}
