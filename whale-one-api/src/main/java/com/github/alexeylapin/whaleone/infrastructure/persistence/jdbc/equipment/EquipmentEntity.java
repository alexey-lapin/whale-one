package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment;

import com.github.alexeylapin.whaleone.domain.model.EquipmentAssemblyPart;
import com.github.alexeylapin.whaleone.domain.model.EquipmentAssemblyScope;
import com.github.alexeylapin.whaleone.domain.model.EquipmentStatus;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.JsonValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
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
    private Instant lastUpdatedAt;
    private long lastUpdatedById;

    private boolean active;
    private EquipmentStatus status;
    private String name;
    private long typeId;
    private String manufacturer;
    private String model;
    private Long deploymentId;
    private Long assemblyId;
    private String assemblyDescriptor;
    private EquipmentAssemblyScope assemblyScope;

    private JsonValue metadata;

    @MappedCollection(idColumn = "assembly_id")
    private Set<EquipmentAssemblyPart> assemblyParts;

    @MappedCollection(idColumn = "equipment_id")
    private Set<EquipmentAttributeEntity> attributes;

}
