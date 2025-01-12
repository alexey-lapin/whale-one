package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentMetadataRepository;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;

@Repository
public class DeploymentMetadataJdbcRepositoryAdapter implements DeploymentMetadataRepository {

    private final DeploymentMetadataJdbcRepository delegate;

    public DeploymentMetadataJdbcRepositoryAdapter(DeploymentMetadataJdbcRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<DeploymentMetadata> findById(long id) {
        return delegate.findById(id)
                .map(this::map);
    }

    @Override
    public DeploymentMetadata save(DeploymentMetadata deploymentMetadata) {
        return map(delegate.save(map(deploymentMetadata)));
    }

    private DeploymentMetadata map(DeploymentMetadataEntity entity) {
        return new DeploymentMetadata(
                entity.deploymentId(),
                entity.version(),
                entity.latitude(),
                entity.longitude(),
                entity.sampleRate(),
                entity.dutyCycleRecord(),
                entity.dutyCycleSleep(),
                entity.dutyCycleInterval(),
                entity.startedAt() == null ? null : entity.startedAt().atZone(ZoneId.systemDefault()),
                entity.stoppedAt() == null ? null : entity.stoppedAt().atZone(ZoneId.systemDefault()),
                entity.recordingStatus(),
                entity.recordingStatusNote()
        );
    }

    private DeploymentMetadataEntity map(DeploymentMetadata deploymentMetadata) {
        return new DeploymentMetadataEntity(
                deploymentMetadata.id(),
                deploymentMetadata.version(),
                deploymentMetadata.latitude(),
                deploymentMetadata.longitude(),
                deploymentMetadata.sampleRate(),
                deploymentMetadata.dutyCycleRecord(),
                deploymentMetadata.dutyCycleSleep(),
                deploymentMetadata.dutyCycleInterval(),
                deploymentMetadata.startedAt() == null ? null : deploymentMetadata.startedAt().toInstant(),
                deploymentMetadata.stoppedAt() == null ? null : deploymentMetadata.stoppedAt().toInstant(),
                deploymentMetadata.recordingStatus(),
                deploymentMetadata.recordingStatusNote()
        );
    }

}
