package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.application.service.impl.DefaultDeploymentService;
import com.github.alexeylapin.whaleone.application.service.impl.DefaultEquipmentService;
import com.github.alexeylapin.whaleone.application.service.impl.DefaultEquipmentTypeService;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeAttributeRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.FieldDescriptor;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.RsqlJdbcVisitor;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.RsqlQuerySpecFactory;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.util.Set;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

    @Bean
    public RSQLParser rsqlParser() {
        var comparisonOperators = RSQLOperators.defaultOperators();
        comparisonOperators.add(new ComparisonOperator("=ilike="));
        return new RSQLParser(comparisonOperators);
    }

    @Bean
    public DefaultEquipmentTypeService equipmentTypeService(ConversionService conversionService,
                                                            EquipmentTypeRepository equipmentTypeRepository) {
        var descriptors = Set.of(
                new FieldDescriptor("name", String.class, "et", "name"),
                new FieldDescriptor("isAssembly", Boolean.class, "et", "is_assembly")
        );
        var querySpecFactory = new RsqlQuerySpecFactory(conversionService, descriptors);
        return new DefaultEquipmentTypeService(equipmentTypeRepository, querySpecFactory);
    }

    @Bean
    public DefaultEquipmentService equipmentService(ConversionService conversionService,
                                                    RSQLParser rsqlParser,
                                                    EquipmentRepository equipmentRepository,
                                                    EquipmentTypeAttributeRepository equipmentTypeAttributeRepository) {
        return new DefaultEquipmentService(equipmentRepository,
                equipmentTypeAttributeRepository,
                rsqlParser,
                fieldDescriptors -> new RsqlJdbcVisitor(conversionService, fieldDescriptors)
        );
    }

    @Bean
    public DefaultDeploymentService deploymentService(EquipmentRepository equipmentRepository,
                                                      DeploymentEquipmentRepository deploymentEquipmentRepository) {
        return new DefaultDeploymentService(equipmentRepository, deploymentEquipmentRepository);
    }

}
