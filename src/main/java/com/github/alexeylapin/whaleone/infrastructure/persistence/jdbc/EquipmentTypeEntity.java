package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Table(value = "equipment_type")
public class EquipmentTypeEntity {

    @Id
    private long id;
    @Version
    private int version;
    private Instant createdAt;
    private long createdById;

    private String name;

    @MappedCollection(idColumn = "equipment_type_id", keyColumn = "order")
    private List<EquipmentTypeAttributeEntity> attributes;

}
