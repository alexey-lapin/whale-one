package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.AnalysisType;
import com.github.alexeylapin.whaleone.domain.repo.AnalysisTypeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
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
public class AnalysisTypeJdbcRepositoryAdapter implements AnalysisTypeRepository {

    private final AnalysisTypeJdbcRepository repository;
    private final AnalysisTypeMapper mapper;

    @Override
    public AnalysisType save(AnalysisType AnalysisType) {
        var entity = mapper.map(AnalysisType);
        entity = repository.save(entity);
        return mapper.map(entity).toBuilder()
                .createdBy(AnalysisType.createdBy())
                .lastUpdatedBy(AnalysisType.lastUpdatedBy())
                .build();
    }

    @Override
    public Optional<AnalysisType> findById(long id) {
        return repository.findOneById(id)
                .map(mapper::map);
    }

    @Override
    public Page<AnalysisType> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage.map(mapper::map));
    }

    @Mapper(config = MappingConfig.class)
    interface AnalysisTypeMapper {

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        AnalysisType map(AnalysisTypeEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        AnalysisType map(AnalysisTypeJdbcRepository.AnalysisTypeProjection source);

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        AnalysisTypeEntity map(AnalysisType source);

    }

}
