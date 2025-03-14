package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaignItem;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DeploymentJdbcRepositoryAdapter implements DeploymentRepository {

    private final DeploymentJdbcRepository repository;
    private final DeploymentMapper mapper;

    @Override
    public Deployment save(Deployment deployment) {
        DeploymentEntity entity = mapper.map(deployment);
        DeploymentEntity savedEntity = repository.save(entity);
        return mapper.map(savedEntity).toBuilder()
                .createdBy(deployment.createdBy())
                .lastUpdatedBy(deployment.lastUpdatedBy())
                .projectRef(deployment.projectRef())
                .projectSiteRef(deployment.projectSiteRef())
                .deploymentCampaignRef(deployment.deploymentCampaignRef())
                .recoveryCampaignRef(deployment.recoveryCampaignRef())
                .build();
    }

    @Override
    public Optional<Deployment> findById(long id) {
        return repository.findOneById(id)
                .map(mapper::map);
    }

    @Override
    public Page<Deployment> findAll(int page,
                                    int size,
                                    String name,
                                    Long projectId,
                                    Long projectSiteId,
                                    String status) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset(), name, projectId, projectSiteId, status);
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(mapper::map));
    }

    @Mapper(config = MappingConfig.class, uses = BaseMapper.class)
    public interface DeploymentMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        @Mapping(source = "projectRef.id", target = "projectId")
        @Mapping(source = "projectSiteRef.id", target = "projectSiteId")
        @Mapping(source = "deploymentCampaignRef.id", target = "deploymentCampaignId")
        @Mapping(source = "recoveryCampaignRef.id", target = "recoveryCampaignId")
        DeploymentEntity map(Deployment source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "projectId", target = "projectRef.id")
        @Mapping(source = "projectSiteId", target = "projectSiteRef.id")
        @Mapping(target = "deploymentCampaignRef", expression = "java(mapDeploymentCampaignRef(source))")
        @Mapping(target = "recoveryCampaignRef", expression = "java(mapRecoveryCampaignRef(source))")
        Deployment map(DeploymentEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        @Mapping(source = "projectId", target = "projectRef.id")
        @Mapping(source = "projectName", target = "projectRef.name")
        @Mapping(source = "projectSiteId", target = "projectSiteRef.id")
        @Mapping(source = "projectSiteName", target = "projectSiteRef.name")
        @Mapping(target = "deploymentCampaignRef", expression = "java(mapDeploymentCampaignRef(source))")
        @Mapping(target = "recoveryCampaignRef", expression = "java(mapRecoveryCampaignRef(source))")
        Deployment map(DeploymentJdbcRepository.DeploymentProjection source);

        default ProjectCampaignItem mapDeploymentCampaignRef(DeploymentJdbcRepository.DeploymentProjection source) {
            if (source.getDeploymentCampaignId() != null) {
                return new ProjectCampaignItem(source.getDeploymentCampaignId(), source.getDeploymentCampaignName());
            }
            return null;
        }

        default ProjectCampaignItem mapRecoveryCampaignRef(DeploymentJdbcRepository.DeploymentProjection source) {
            if (source.getRecoveryCampaignId() != null) {
                return new ProjectCampaignItem(source.getRecoveryCampaignId(), source.getRecoveryCampaignName());
            }
            return null;
        }

        default ProjectCampaignItem mapDeploymentCampaignRef(DeploymentEntity source) {
            if (source.getDeploymentCampaignId() != null) {
                return new ProjectCampaignItem(source.getDeploymentCampaignId(), null);
            }
            return null;
        }

        default ProjectCampaignItem mapRecoveryCampaignRef(DeploymentEntity source) {
            if (source.getRecoveryCampaignId() != null) {
                return new ProjectCampaignItem(source.getRecoveryCampaignId(), null);
            }
            return null;
        }

    }

}
