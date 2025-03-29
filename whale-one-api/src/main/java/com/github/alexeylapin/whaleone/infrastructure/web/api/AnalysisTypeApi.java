package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.alexeylapin.whaleone.domain.model.AnalysisType;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.AnalysisTypeRepository;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import com.github.alexeylapin.whaleone.infrastructure.web.api.serde.RawJsonDeserializer;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AnalysisTypeApi {

    private final AnalysisTypeRepository analysisTypeRepository;
    private final AnalysisTypeMapper mapper;

    @PostMapping("/analysis/types")
    public AnalysisTypeDto create(@RequestBody AnalysisTypeDto analysisTypeDto,
                                  @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        var analysisType = mapper.fromDto(analysisTypeDto).toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .build();
        var savedAnalysisType = analysisTypeRepository.save(analysisType);
        return mapper.toDto(savedAnalysisType);
    }

    @PutMapping("/analysis/types/{id}")
    public AnalysisTypeDto update(@PathVariable long id,
                                  @RequestBody AnalysisTypeDto analysisTypeDto,
                                  @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(analysisTypeDto.version() > 0,
                "project.version must be greater than 0 - existing project expected");
        Assert.isTrue(id == analysisTypeDto.id(),
                "id must match");
        var analysisType = mapper.fromDto(analysisTypeDto).toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        var savedAnalysisType = analysisTypeRepository.save(analysisType);
        return mapper.toDto(savedAnalysisType);
    }

    @GetMapping("/analysis/types/{id}")
    public AnalysisTypeDto get(@PathVariable long id) {
        var analysisType = analysisTypeRepository.findById(id).orElseThrow();
        return mapper.toDto(analysisType);
    }

    @GetMapping("/analysis/types")
    public PageDto<AnalysisTypeDto> getAll(@RequestParam int page,
                                           @RequestParam int size) {
        Page<AnalysisType> aPage = analysisTypeRepository.findAll(page, size);
        return new PageDto<>(aPage.map(mapper::toDto).getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    public record AnalysisTypeDto(
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
    interface AnalysisTypeMapper {

        AnalysisType fromDto(AnalysisTypeDto source);

        AnalysisTypeDto toDto(AnalysisType source);

    }

}
