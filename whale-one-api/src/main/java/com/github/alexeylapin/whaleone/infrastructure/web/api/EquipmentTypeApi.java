package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeItem;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeAttributeRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeDeploymentAttributeRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import com.github.alexeylapin.whaleone.infrastructure.web.api.serde.RawJsonDeserializer;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EquipmentTypeApi {

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentTypeAttributeRepository equipmentTypeAttributeRepository;
    private final EquipmentTypeDeploymentAttributeRepository equipmentTypeDeploymentAttributeRepository;
    private final EquipmentTypeMapper equipmentTypeMapper;
    private final EquipmentTypeAttributeMapper attributeMapper;

    @PostMapping("/equipment/types")
    public EquipmentTypeDto create(@RequestBody EquipmentTypeDto equipmentTypeDto,
                                   @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        EquipmentType equipmentType = equipmentTypeMapper.fromDto(equipmentTypeDto).toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .build();
        EquipmentType savedEquipmentType = equipmentTypeRepository.save(equipmentType);
        return equipmentTypeMapper.toDto(savedEquipmentType);
    }

    @PutMapping("/equipment/types/{id}")
    public EquipmentTypeDto update(@PathVariable long id,
                                   @RequestBody EquipmentTypeDto equipmentTypeDto,
                                   @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing equipmentType expected");
        Assert.isTrue(equipmentTypeDto.version() > 0,
                "equipmentType.version must be greater than 0 - existing equipmentType expected");
        Assert.isTrue(id == equipmentTypeDto.id(),
                "id must match");
        EquipmentType equipmentType = equipmentTypeMapper.fromDto(equipmentTypeDto).toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        EquipmentType savedEquipmentType = equipmentTypeRepository.save(equipmentType);
        return equipmentTypeMapper.toDto(savedEquipmentType);
    }

    @GetMapping("/equipment/types/{id}")
    public EquipmentTypeDto get(@PathVariable long id) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id).orElseThrow();
        return equipmentTypeMapper.toDto(equipmentType);
    }

    @GetMapping("/equipment/types")
    public PageDto<EquipmentType> getAll(@RequestParam int page, @RequestParam int size) {
        var aPage = equipmentTypeRepository.findAll(page, size);
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    @GetMapping("/equipment/types/items")
    public List<EquipmentTypeItem> getAllItems(@RequestParam Optional<String> q) {
        return equipmentTypeRepository.findAllItems(q.orElse(""));
    }

    // Equipment Attributes

    @PostMapping("/equipment/types/{id}/attributes/equipment")
    public EquipmentTypeAttributeDto createEquipmentAttribute(@PathVariable long id,
                                                              @RequestBody EquipmentTypeAttributeDto attributeDto) {
        var attribute = attributeMapper.fromDto(attributeDto).toBuilder()
                .id(0)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeAttributeRepository.save(attribute);
        return attributeMapper.toDto(savedAttribute);
    }

    @PutMapping("/equipment/types/{id}/attributes/equipment/{attributeId}")
    public EquipmentTypeAttributeDto updateEquipmentAttribute(@PathVariable long id,
                                                              @PathVariable long attributeId,
                                                              @RequestBody EquipmentTypeAttributeDto attributeDto) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(attributeId > 0,
                "project must be greater than 0 - existing attribute expected");
        Assert.isTrue(attributeId == attributeDto.id(),
                "project must match");
        var attribute = attributeMapper.fromDto(attributeDto).toBuilder()
                .id(attributeId)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeAttributeRepository.save(attribute);
        return attributeMapper.toDto(savedAttribute);
    }

    @GetMapping("/equipment/types/{id}/attributes/equipment")
    public List<EquipmentTypeAttributeDto> getAllEquipmentAttributes(@PathVariable long id) {
        return equipmentTypeAttributeRepository.findAll(id)
                .map(attributeMapper::toDto)
                .getContent();
    }

    @DeleteMapping("/equipment/types/{id}/attributes/equipment/{attributeId}")
    public void deleteEquipmentAttribute(@PathVariable long id, @PathVariable long attributeId) {
        equipmentTypeAttributeRepository.deleteById(attributeId);
    }

    // Deployment Attributes

    @PostMapping("/equipment/types/{id}/attributes/deployment")
    public EquipmentTypeAttributeDto createDeploymentAttribute(@PathVariable long id,
                                                               @RequestBody EquipmentTypeAttributeDto attributeDto) {
        var attribute = attributeMapper.fromDto(attributeDto).toBuilder()
                .id(0)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeDeploymentAttributeRepository.save(attribute);
        return attributeMapper.toDto(savedAttribute);
    }

    @PutMapping("/equipment/types/{id}/attributes/deployment/{attributeId}")
    public EquipmentTypeAttributeDto updateDeploymentAttribute(@PathVariable long id,
                                                               @PathVariable long attributeId,
                                                               @RequestBody EquipmentTypeAttributeDto attributeDto) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(attributeId > 0,
                "project must be greater than 0 - existing attribute expected");
        Assert.isTrue(attributeId == attributeDto.id(),
                "project must match");
        var attribute = attributeMapper.fromDto(attributeDto).toBuilder()
                .id(attributeId)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeDeploymentAttributeRepository.save(attribute);
        return attributeMapper.toDto(savedAttribute);
    }

    @GetMapping("/equipment/types/{id}/attributes/deployment")
    public List<EquipmentTypeAttributeDto> getAllDeploymentAttributes(@PathVariable long id) {
        return equipmentTypeDeploymentAttributeRepository.findAll(id)
                .map(attributeMapper::toDto)
                .getContent();
    }

    @DeleteMapping("/equipment/types/{id}/attributes/deployment/{attributeId}")
    public void deleteDeploymentAttribute(@PathVariable long id, @PathVariable long attributeId) {
        equipmentTypeDeploymentAttributeRepository.deleteById(attributeId);
    }

    public record EquipmentTypeAttributeDto(
            long id,
            int version,
            long equipmentTypeId,
            String name,
            String description,
            int order,
            String type,
            @JsonRawValue
            @JsonDeserialize(using = RawJsonDeserializer.class)
            String metadata
    ) {
    }

    public record EquipmentTypeDto(
            long id,
            int version,
            ZonedDateTime createdAt,
            UserRef createdBy,
            ZonedDateTime lastUpdatedAt,
            UserRef lastUpdatedBy,

            String name,
            String description,
            @JsonRawValue
            @JsonDeserialize(using = RawJsonDeserializer.class)
            String metadata
    ) {
    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentTypeAttributeMapper {

        EquipmentTypeAttribute fromDto(EquipmentTypeAttributeDto source);

        EquipmentTypeAttributeDto toDto(EquipmentTypeAttribute source);

    }

    @Mapper(config = MappingConfig.class)
    interface EquipmentTypeMapper {

        EquipmentType fromDto(EquipmentTypeDto source);

        EquipmentTypeDto toDto(EquipmentType source);

    }

}
