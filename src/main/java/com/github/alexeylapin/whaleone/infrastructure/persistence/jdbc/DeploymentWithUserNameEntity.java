package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeploymentWithUserNameEntity extends DeploymentEntity {

    private String createdByName;

}
