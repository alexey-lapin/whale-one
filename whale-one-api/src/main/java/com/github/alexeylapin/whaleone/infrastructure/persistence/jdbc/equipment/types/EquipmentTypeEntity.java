package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.ZonedDateTime;

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
    private Instant lastUpdatedAt;
    private long lastUpdatedById;

    private String name;

}
