package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "equipment_assembly_part")
public class EquipmentAssemblyPartEntity {

    private long typeId;
    private long equipmentId;

}
