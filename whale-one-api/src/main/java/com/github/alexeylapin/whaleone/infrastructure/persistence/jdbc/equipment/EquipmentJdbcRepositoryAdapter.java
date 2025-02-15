package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentJdbcRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJdbcRepository repository;

    public EquipmentJdbcRepositoryAdapter(EquipmentJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = EquipmentMapper.INSTANCE.map(equipment);
        entity = repository.save(entity);
        return EquipmentMapper.INSTANCE.map(entity).toBuilder()
                .createdBy(equipment.createdBy())
                .type(equipment.type())
                .build();
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return repository.findOneById(id)
                .map(EquipmentMapper.INSTANCE::map);
    }

    @Override
    public Page<EquipmentListElement> findAllElements(int page, int size, String name, Long typeId) {
        var pageable = PageRequest.of(page, size);
        var items = repository.findAllElements(pageable.getPageSize(), pageable.getOffset(), name, typeId);
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage);
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
//        return repository.findAllByDeploymentId(id).stream()
//                .map(EquipmentJdbcRepositoryAdapter::map)
//                .toList();
        throw new UnsupportedOperationException();
    }

    @Mapper(uses = BaseMapper.class)
    interface EquipmentMapper {

        EquipmentMapper INSTANCE = Mappers.getMapper(EquipmentMapper.class);

        @Mapping(source = "type.id", target = "typeId")
        @Mapping(source = "createdBy.id", target = "createdById")
        EquipmentEntity map(Equipment source);

//        default Instant map(ZonedDateTime value) {
//            return value.toInstant();
//        }

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "typeId", target = "type.id")
        Equipment map(EquipmentEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "typeId", target = "type.id")
        @Mapping(source = "typeName", target = "type.name")
        Equipment map(EquipmentJdbcRepository.EquipmentProjection source);

//        default ZonedDateTime map(Instant value) {
//            return value.atZone(ZoneId.systemDefault());
//        }

    }

}
