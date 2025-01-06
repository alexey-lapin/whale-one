package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@Table(value = "equipment_type")
public class JdbcEquipmentTypeEntity {

    @Id
    private int id;

    private String name;

    @MappedCollection(idColumn = "equipment_type_id", keyColumn = "order")
    private List<JdbcEquipmentTypeAttributeEntity> attributes;

}
