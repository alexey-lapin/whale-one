package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@Table(value = "deployment")
public class DeploymentEntity {

    @Id
    private long id;
    @Version
    private int version;
    private Instant createdAt;
    private long createdById;
    private Instant lastUpdatedAt;
    private long lastUpdatedById;

    private DeploymentStatus status;
    private String name;
    private String description;
    private long projectId;
    private long projectSiteId;
    private String platform;
    private String providerOrganisations;
    private String providerParticipants;

    private Double latitude;
    private Double longitude;
    private Double bathymetry;
    private Instant deployedAt;
    private Long deploymentCampaignId;
    private Instant firstFileAt;
    private Instant lastFileAt;

    private String recoveryStatus;
    private Instant recoveredAt;
    private Long recoveryCampaignId;

}
