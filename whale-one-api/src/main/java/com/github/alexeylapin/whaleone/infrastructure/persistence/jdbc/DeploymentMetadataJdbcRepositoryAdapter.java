package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentMetadataRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;

@Repository
public class DeploymentMetadataJdbcRepositoryAdapter implements DeploymentMetadataRepository {

    private final DeploymentMetadataJdbcRepository repository;

    public DeploymentMetadataJdbcRepositoryAdapter(DeploymentMetadataJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeploymentMetadata save(DeploymentMetadata deploymentMetadata) {
        return map(repository.save(map(deploymentMetadata)));
    }

    @Override
    public Page<DeploymentMetadata> findAll(int page, int size) {
        var aPage = repository.findAll(PageRequest.of(page, size, Sort.by("deploymentId").descending()));
        return new DefaultPage<>(aPage.map(this::map));
    }

    @Override
    public Optional<DeploymentMetadata> findById(long id) {
        return repository.findById(id)
                .map(this::map);
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
