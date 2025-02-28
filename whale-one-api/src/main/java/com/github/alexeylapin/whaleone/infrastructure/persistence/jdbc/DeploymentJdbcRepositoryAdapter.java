package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DeploymentJdbcRepositoryAdapter implements DeploymentRepository {

    private static final DeploymentMapper MAPPER = Mappers.getMapper(DeploymentMapper.class);

    private final DeploymentJdbcRepository repository;

    @Override
    public Deployment save(Deployment deployment) {
        DeploymentEntity entity = MAPPER.map(deployment);
        DeploymentEntity savedEntity = repository.save(entity);
        return MAPPER.map(savedEntity).toBuilder()
                .createdBy(deployment.createdBy())
                .projectRef(deployment.projectRef())
                .projectSiteRef(deployment.projectSiteRef())
                .build();
    }

    @Override
    public Optional<Deployment> findById(long id) {
        return repository.findOneById(id)
                .map(MAPPER::map);
    }

    @Override
    public Page<Deployment> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(MAPPER::map));
    }

    @Mapper(uses = BaseMapper.class)
    interface DeploymentMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
//        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        @Mapping(source = "projectRef.id", target = "projectId")
        @Mapping(source = "projectSiteRef.id", target = "projectSiteId")
        DeploymentEntity map(Deployment deployment);

        Deployment map(DeploymentEntity entity);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
//        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
//        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        @Mapping(source = "projectId", target = "projectRef.id")
        @Mapping(source = "projectName", target = "projectRef.name")
        @Mapping(source = "projectSiteId", target = "projectSiteRef.id")
        @Mapping(source = "projectSiteName", target = "projectSiteRef.name")
        Deployment map(DeploymentJdbcRepository.DeploymentProjection entity);

    }

}
