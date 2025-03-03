package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.JsonValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "equipment_type_deployment_attribute")
public class EquipmentTypeDeploymentAttributeEntity {

    @Id
    private long id;
    @Version
    private int version;
    private long equipmentTypeId;

    private String name;
    private String description;
    private int order;
    private String type;
    private JsonValue metadata;

}
