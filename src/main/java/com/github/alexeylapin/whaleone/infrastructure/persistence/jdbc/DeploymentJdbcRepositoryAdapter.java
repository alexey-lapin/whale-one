package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;

@Repository
public class DeploymentJdbcRepositoryAdapter implements DeploymentRepository {

    private final DeploymentJdbcRepository repository;

    public DeploymentJdbcRepositoryAdapter(DeploymentJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Deployment save(Deployment deployment) {
        DeploymentEntity entity = new DeploymentEntity();
        entity.setId(deployment.id());
        entity.setVersion(deployment.version());
        entity.setCreatedAt(deployment.createdAt().toInstant());
        entity.setCreatedById(deployment.createdById());
        entity.setName(deployment.name());
        entity.setDescription(deployment.description());
        entity.setStatus(deployment.status());
        entity = repository.save(entity);
        return map(entity).toBuilder()
                .createdBy(deployment.createdBy())
                .build();
    }

    @Override
    public Optional<Deployment> findById(long id) {
        return repository.findOneById(id)
                .map(DeploymentJdbcRepositoryAdapter::map);
    }

    @Override
    public Page<Deployment> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(DeploymentJdbcRepositoryAdapter::map));
    }

    private static Deployment map(DeploymentWithUserNameEntity entity) {
        return map((DeploymentEntity) entity).toBuilder()
                .createdBy(entity.getCreatedByName())
                .build();
    }

    private static Deployment map(DeploymentEntity entity) {
        return Deployment.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                .createdById(entity.getCreatedById())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

}
