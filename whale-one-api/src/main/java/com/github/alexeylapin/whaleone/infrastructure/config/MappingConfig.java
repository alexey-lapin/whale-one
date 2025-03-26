package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.BaseMapper;
import org.mapstruct.MapperConfig;

@MapperConfig(componentModel = "spring", uses = BaseMapper.class)
public interface MappingConfig {
}
