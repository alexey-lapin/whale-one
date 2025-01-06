package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
public record EquipmentType(
        int id,
        String name,
        List<EquipmentTypeAttribute> attributes
) {
    public EquipmentType(int id, String name, List<EquipmentTypeAttribute> attributes) {
        this.id = id;
        this.name = name;
        this.attributes = attributes == null ? new ArrayList<>() : attributes;
    }
}
