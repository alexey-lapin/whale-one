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
    private final EquipmentTypeAttributeMapper mapper;

    @PostMapping("/equipment/types")
    public EquipmentType create(@RequestBody EquipmentType equipmentType,
                                @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        equipmentType = equipmentType.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .build();
        return equipmentTypeRepository.save(equipmentType);
    }

    @PutMapping("/equipment/types/{id}")
    public EquipmentType update(@PathVariable long id,
                                @RequestBody EquipmentType equipmentType,
                                @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing equipmentType expected");
        Assert.isTrue(equipmentType.version() > 0,
                "equipmentType.version must be greater than 0 - existing equipmentType expected");
        Assert.isTrue(id == equipmentType.id(),
                "id must match");
        equipmentType = equipmentType.toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        return equipmentTypeRepository.save(equipmentType);
    }

    @GetMapping("/equipment/types/{id}")
    public EquipmentType get(@PathVariable long id) {
        return equipmentTypeRepository.findById(id).orElseThrow();
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
    public EquipmentTypeAttributeDto createSite(@PathVariable long id,
                                                @RequestBody EquipmentTypeAttributeDto attributeDto) {
        var attribute = mapper.fromDto(attributeDto).toBuilder()
                .id(0)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeAttributeRepository.save(attribute);
        return mapper.toDto(savedAttribute);
    }

    @GetMapping("/equipment/types/{id}/attributes/equipment")
    public List<EquipmentTypeAttributeDto> getAllSites(@PathVariable long id) {
        return equipmentTypeAttributeRepository.findAll(id)
                .map(mapper::toDto)
                .getContent();
    }

//    @GetMapping("/equipment/types/{id}/attributes/items")
//    public List<ProjectSiteItem> getAllSiteItems(@PathVariable long id, @RequestParam Optional<String> q) {
//        return equipmentTypeAttributeRepository.findAllItems(id, q.orElse(""));
//    }

    @PutMapping("/equipment/types/{id}/attributes/equipment/{attributeId}")
    public EquipmentTypeAttributeDto updateAttribute(@PathVariable long id,
                                                     @PathVariable long attributeId,
                                                     @RequestBody EquipmentTypeAttributeDto attributeDto) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(attributeId > 0,
                "project must be greater than 0 - existing attribute expected");
        Assert.isTrue(attributeId == attributeDto.id(),
                "project must match");
        var attribute = mapper.fromDto(attributeDto).toBuilder()
                .id(attributeId)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeAttributeRepository.save(attribute);
        return mapper.toDto(savedAttribute);
    }

    @DeleteMapping("/equipment/types/{id}/attributes/equipment/{attributeId}")
    public void deleteSite(@PathVariable long id, @PathVariable long attributeId) {
        equipmentTypeAttributeRepository.deleteById(attributeId);
    }

    // Deployment Attributes

    @PostMapping("/equipment/types/{id}/attributes/deployment")
    public EquipmentTypeAttributeDto createDeploymentAttribute(@PathVariable long id,
                                                               @RequestBody EquipmentTypeAttributeDto attributeDto) {
        var attribute = mapper.fromDto(attributeDto).toBuilder()
                .id(0)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeDeploymentAttributeRepository.save(attribute);
        return mapper.toDto(savedAttribute);
    }

    @GetMapping("/equipment/types/{id}/attributes/deployment")
    public List<EquipmentTypeAttributeDto> getAllDeploymentAttributes(@PathVariable long id) {
        return equipmentTypeDeploymentAttributeRepository.findAll(id)
                .map(mapper::toDto)
                .getContent();
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
        var attribute = mapper.fromDto(attributeDto).toBuilder()
                .id(attributeId)
                .equipmentTypeId(id)
                .build();
        var savedAttribute = equipmentTypeDeploymentAttributeRepository.save(attribute);
        return mapper.toDto(savedAttribute);
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

    @Mapper(config = MappingConfig.class)
    interface EquipmentTypeAttributeMapper {

        EquipmentTypeAttribute fromDto(EquipmentTypeAttributeDto attribute);

        EquipmentTypeAttributeDto toDto(EquipmentTypeAttribute attribute);

    }

}
