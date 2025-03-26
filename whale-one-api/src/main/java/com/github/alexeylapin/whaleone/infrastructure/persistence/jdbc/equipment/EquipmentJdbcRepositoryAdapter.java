package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@RegisterReflection(
        classes = EquipmentJdbcRepository.EquipmentListElementRowMapper.class,
        memberCategories = {
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS
        }
)
public class EquipmentJdbcRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJdbcRepository repository;
    private final EquipmentMapper mapper;

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = mapper.map(equipment);
        entity = repository.save(entity);
        return mapper.map(entity).toBuilder()
                .createdBy(equipment.createdBy())
                .lastUpdatedBy(equipment.lastUpdatedBy())
                .type(equipment.type())
                .build();
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return repository.findOneById(id)
                .map(mapper::map);
    }

    @Override
    public Page<EquipmentListElement> findAllElements(int page,
                                                      int size,
                                                      String name,
                                                      Long typeId,
                                                      String manufacturer,
                                                      String model) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAllElements(pageable.getPageSize(),
                pageable.getOffset(),
                name,
                typeId,
                manufacturer,
                model);
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage);
    }

    @Override
    public List<EquipmentItem> findAllItems(long typeId, String nameQuery, boolean includeAllocated) {
        var pageable = PageRequest.of(0, 50);
        return repository.findAllItems(
                pageable.getPageSize(),
                pageable.getOffset(),
                typeId,
                nameQuery,
                includeAllocated);
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
//        return repository.findAllByDeploymentId(id).stream()
//                .map(EquipmentJdbcRepositoryAdapter::map)
//                .toList();
        throw new UnsupportedOperationException();
    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        @Mapping(source = "type.id", target = "typeId")
        EquipmentEntity map(Equipment source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "typeId", target = "type.id")
        Equipment map(EquipmentEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        @Mapping(source = "typeId", target = "type.id")
        @Mapping(source = "typeName", target = "type.name")
        Equipment map(EquipmentJdbcRepository.EquipmentProjection source);

    }

}
