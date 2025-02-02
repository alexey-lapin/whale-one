package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "equipment_type_attribute")
public class EquipmentTypeAttributeEntity {

    @Id
    private long id;
    private String name;

}
