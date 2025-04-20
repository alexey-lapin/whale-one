package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.application.service.impl.DefaultEquipmentService;
import com.github.alexeylapin.whaleone.application.service.impl.DefaultEquipmentTypeService;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.FilterFieldDescriptor;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.RsqlQuerySpecFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.util.Set;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

    @Bean
    public DefaultEquipmentTypeService equipmentTypeService(ConversionService conversionService,
                                                            EquipmentTypeRepository equipmentTypeRepository) {
        var descriptors = Set.of(
                new FilterFieldDescriptor("name", String.class, "et", "name"),
                new FilterFieldDescriptor("isAssembly", Boolean.class, "et", "is_assembly")
        );
        var querySpecFactory = new RsqlQuerySpecFactory(conversionService, descriptors);
        return new DefaultEquipmentTypeService(equipmentTypeRepository, querySpecFactory);
    }

    @Bean
    public DefaultEquipmentService equipmentService(ConversionService conversionService,
                                                    EquipmentRepository equipmentRepository) {
        var descriptors = Set.of(
                new FilterFieldDescriptor("name", String.class, "e", "name"),
                new FilterFieldDescriptor("typeId", Integer.class, "e", "type_id"),
                new FilterFieldDescriptor("manufacturer", String.class, "e", "manufacturer"),
                new FilterFieldDescriptor("model", String.class, "e", "model"),
                new FilterFieldDescriptor("active", Boolean.class, "e", "active")
        );
        var querySpecFactory = new RsqlQuerySpecFactory(conversionService, descriptors);
        return new DefaultEquipmentService(equipmentRepository, querySpecFactory);
    }

}
