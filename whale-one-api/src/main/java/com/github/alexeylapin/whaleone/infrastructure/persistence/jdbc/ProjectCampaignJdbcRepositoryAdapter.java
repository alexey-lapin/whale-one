package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectCampaign;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaignItem;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectCampaignRepository;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectCampaignJdbcRepositoryAdapter implements ProjectCampaignRepository {

    private static final ProjectCampaignMapper MAPPER = Mappers.getMapper(ProjectCampaignMapper.class);

    private final ProjectCampaignJdbcRepository repository;

    @Override
    public ProjectCampaign save(ProjectCampaign projectCampaign) {
        ProjectCampaignEntity entity = MAPPER.map(projectCampaign);
        ProjectCampaignEntity savedEntity = repository.save(entity);
        return MAPPER.map(savedEntity);
    }

    @Override
    public Optional<ProjectCampaign> findById(long id) {
        return repository.findById(id).map(MAPPER::map);
    }

    @Override
    public Page<ProjectCampaign> findAll(long projectId) {
        var page = repository.findAllByProjectId(projectId, Pageable.unpaged(Sort.by("id")));
        return new DefaultPage<>(page.map(MAPPER::map));
    }

    @Override
    public List<ProjectCampaignItem> findAllItems(long projectId, String nameQuery) {
        var pageable = PageRequest.of(0, 50, Sort.by("id").descending());
        return repository.findAllByProjectIdAndNameContainingIgnoreCase(projectId, nameQuery, pageable).getContent();
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Mapper(uses = BaseMapper.class)
    interface ProjectCampaignMapper {

        ProjectCampaignEntity map(ProjectCampaign source);

        ProjectCampaign map(ProjectCampaignEntity source);

    }

}
