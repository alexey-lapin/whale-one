package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "equipment_attribute")
public class EquipmentAttributeEntity {

    @Id
    private long id;
    private long equipmentTypeAttributeId;
    private String value;

}
