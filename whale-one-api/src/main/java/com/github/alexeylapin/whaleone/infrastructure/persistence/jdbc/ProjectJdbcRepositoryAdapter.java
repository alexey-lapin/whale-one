package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Project;
import com.github.alexeylapin.whaleone.domain.model.ProjectItem;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectJdbcRepositoryAdapter implements ProjectRepository {

    private final ProjectJdbcRepository delegate;

    @Override
    public Project save(Project project) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.id());
        entity.setVersion(project.version());
        entity.setCreatedAt(project.createdAt().toInstant());
        entity.setCreatedById(project.createdById());
        entity.setName(project.name());
        entity.setDescription(project.description());
        entity = delegate.save(entity);
        return map(entity).toBuilder()
                .createdBy(project.createdBy())
                .build();
    }

    @Override
    public Optional<Project> findById(long id) {
        return delegate.findOneById(id)
                .map(ProjectJdbcRepositoryAdapter::map);
    }

    @Override
    public Page<Project> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = delegate.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, delegate::count);
        return new DefaultPage<>(aPage.map(ProjectJdbcRepositoryAdapter::map));
    }

    @Override
    public List<ProjectItem> findAllItems(String nameQuery) {
        var pageable = PageRequest.of(0, 50, Sort.by("id").descending());
        return delegate.findAllByNameContainingIgnoreCase(nameQuery, pageable).getContent();
    }

    private static Project map(ProjectJdbcRepository.ProjectWithUserNameEntity entity) {
        return map((ProjectEntity) entity).toBuilder()
                .createdBy(entity.getCreatedByName())
                .build();
    }

    private static Project map(ProjectEntity entity) {
        return Project.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()))
                .createdById(entity.getCreatedById())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

}
