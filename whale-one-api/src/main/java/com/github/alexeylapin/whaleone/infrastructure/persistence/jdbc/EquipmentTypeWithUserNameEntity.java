package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentTypeWithUserNameEntity extends EquipmentTypeEntity {

    private String createdByName;

}
