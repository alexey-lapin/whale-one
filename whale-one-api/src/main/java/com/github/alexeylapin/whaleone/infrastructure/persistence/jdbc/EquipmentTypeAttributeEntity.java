package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "equipment_type_attribute")
public class EquipmentTypeAttributeEntity {

    @Id
    private long id;
    @Version
    private int version;
    private long equipmentTypeId;

    private String name;
    private String description;
    private String type;
    private JsonValue metadata;

}
