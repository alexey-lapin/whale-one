package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.repository.ListCrudRepository;

public interface DeploymentMetadataJdbcRepository extends ListCrudRepository<JdbcDeploymentMetadataEntity, Long> {
}
