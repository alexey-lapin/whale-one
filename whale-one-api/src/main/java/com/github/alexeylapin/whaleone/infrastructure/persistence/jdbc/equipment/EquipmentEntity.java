package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@Table(value = "equipment")
public class EquipmentEntity {

    @Id
    private long id;
    @Version
    private int version;
    private Instant createdAt;
    private long createdById;

    private boolean active;
    private String name;
    private long typeId;
    private Long deploymentId;

    @MappedCollection(idColumn = "equipment_id")
    private Set<EquipmentAttributeEntity> attributes;

}
