package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Project;
import com.github.alexeylapin.whaleone.domain.model.ProjectItem;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectRepository;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProjectJdbcRepositoryAdapter implements ProjectRepository {

    private static final ProjectMapper MAPPER = Mappers.getMapper(ProjectMapper.class);

    private final ProjectJdbcRepository repository;

    @Override
    public Project save(Project project) {
        ProjectEntity entity = MAPPER.map(project);
        entity = repository.save(entity);
        return MAPPER.map(entity).toBuilder()
                .createdBy(project.createdBy())
                .lastUpdatedBy(project.lastUpdatedBy())
                .build();
    }

    @Override
    public Optional<Project> findById(long id) {
        return repository.findOneById(id)
                .map(MAPPER::map);
    }

    @Override
    public Page<Project> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(MAPPER::map));
    }

    @Override
    public List<ProjectItem> findAllItems(String nameQuery) {
        var pageable = PageRequest.of(0, 50, Sort.by("id").descending());
        return repository.findAllByNameContainingIgnoreCase(nameQuery, pageable).getContent();
    }

    @Mapper(uses = BaseMapper.class)
    interface ProjectMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        ProjectEntity map(Project source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        Project map(ProjectEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        Project map(ProjectJdbcRepository.ProjectProjection source);

    }

}
