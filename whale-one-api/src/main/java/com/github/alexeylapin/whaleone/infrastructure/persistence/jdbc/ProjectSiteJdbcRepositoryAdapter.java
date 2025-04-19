package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectSite;
import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectSiteRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectSiteJdbcRepositoryAdapter implements ProjectSiteRepository {

    private final ProjectSiteJdbcRepository repository;
    private final ProjectSiteMapper mapper;

    @Override
    public ProjectSite save(ProjectSite projectSite) {
        ProjectSiteEntity entity = mapper.map(projectSite);
        ProjectSiteEntity savedEntity = repository.save(entity);
        return mapper.map(savedEntity);
    }

    @Override
    public Optional<ProjectSite> findById(long id) {
        return repository.findById(id).map(mapper::map);
    }

    @Override
    public Page<ProjectSite> findAll(long projectId) {
        var page = repository.findAllByProjectId(projectId, Pageable.unpaged(Sort.by("id")));
        return new DefaultPage<>(page.map(mapper::map));
    }

    @Override
    public List<ProjectSiteItem> findAllItems(long projectId, String nameQuery) {
        var pageable = PageRequest.of(0, 50, Sort.by("id").descending());
        return repository.findAllByProjectIdAndNameContainingIgnoreCase(projectId, nameQuery, pageable).getContent();
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Mapper(config = MappingConfig.class)
    interface ProjectSiteMapper {

        ProjectSiteEntity map(ProjectSite source);

        ProjectSite map(ProjectSiteEntity source);

    }

}
