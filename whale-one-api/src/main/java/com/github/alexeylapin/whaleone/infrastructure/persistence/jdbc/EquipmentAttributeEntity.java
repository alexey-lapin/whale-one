package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(value = "equipment_attribute")
public class EquipmentAttributeEntity {

    @Id
    private long id;
    private long equipmentTypeAttributeId;
    private String value;

}
