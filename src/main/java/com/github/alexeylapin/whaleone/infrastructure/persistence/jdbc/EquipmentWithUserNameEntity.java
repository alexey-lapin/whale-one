package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentWithUserNameEntity extends EquipmentEntity {

    private String createdByName;

}
