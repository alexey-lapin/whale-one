package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.ProjectSite;
import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectSiteJdbcRepositoryAdapter implements ProjectSiteRepository {

    private final ProjectSiteJdbcRepository delegate;

    @Override
    public ProjectSite save(ProjectSite projectSite) {
        ProjectSiteEntity entity = new ProjectSiteEntity();
        entity.setId(projectSite.id());
        entity.setProjectId(projectSite.projectId());
        entity.setName(projectSite.name());
        entity.setLongitude(projectSite.longitude());
        entity.setLatitude(projectSite.latitude());
        entity.setDepth(projectSite.depth());
        entity = delegate.save(entity);
        return map(entity);
    }

    @Override
    public Optional<ProjectSite> findById(long id) {
        return delegate.findById(id).map(ProjectSiteJdbcRepositoryAdapter::map);
    }

    @Override
    public Page<ProjectSite> findAll(long projectId) {
        var page = delegate.findAllByProjectId(projectId, Pageable.unpaged(Sort.by("id")));
        return new DefaultPage<>(page.map(ProjectSiteJdbcRepositoryAdapter::map));
    }

    @Override
    public List<ProjectSiteItem> findAllItems(long projectId, String nameQuery) {
        var pageable = PageRequest.of(0, 50, Sort.by("id").descending());
        return delegate.findAllByProjectIdAndNameContainingIgnoreCase(projectId, nameQuery, pageable).getContent();
    }

    @Override
    public void deleteById(long id) {
        delegate.deleteById(id);
    }

    private static ProjectSite map(ProjectSiteEntity entity) {
        return ProjectSite.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .name(entity.getName())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .depth(entity.getDepth())
                .build();
    }

}
