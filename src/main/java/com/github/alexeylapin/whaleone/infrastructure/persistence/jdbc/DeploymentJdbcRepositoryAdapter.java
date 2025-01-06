package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class DeploymentJdbcRepositoryAdapter implements DeploymentRepository {

    private final DeploymentJdbcRepository repository;

    public DeploymentJdbcRepositoryAdapter(DeploymentJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Deployment> findAll() {
        return repository.findAll().stream()
                .map(entity -> Deployment.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .status(entity.getStatus())
                        .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                        .createdBy(entity.getCreatedBy())
                        .build())
                .toList();
    }

    @Override
    public Optional<Deployment> findById(long id) {
        return repository.findById(id)
                .map(entity -> Deployment.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .status(entity.getStatus())
                        .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                        .createdBy(entity.getCreatedBy())
                        .build());
    }

    @Override
    public Deployment save(Deployment deployment) {
        JdbcDeploymentEntity entity = new JdbcDeploymentEntity();
        entity.setId(deployment.id());
        entity.setName(deployment.name());
        entity.setDescription(deployment.description());
        entity.setStatus(deployment.status());
        entity.setCreatedAt(deployment.createdAt().toInstant());
        entity.setCreatedBy(deployment.createdBy());
        entity = repository.save(entity);
        deployment = deployment.toBuilder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                .createdBy(entity.getCreatedBy())
                .build();
        return deployment;
    }

}
