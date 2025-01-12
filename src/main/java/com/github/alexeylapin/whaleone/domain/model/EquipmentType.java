package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
public record EquipmentType(
        long id,
        int version,
        ZonedDateTime createdAt,
        long createdById,
        String createdBy,

        String name,
        List<EquipmentTypeAttribute> attributes
) {
//    public EquipmentType(long id, String name, List<EquipmentTypeAttribute> attributes) {
//        this.id = id;
//        this.name = name;
//        this.attributes = attributes == null ? new ArrayList<>() : attributes;
//    }
}
