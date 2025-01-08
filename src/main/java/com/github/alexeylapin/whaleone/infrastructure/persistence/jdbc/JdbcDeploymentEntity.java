package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
@Table(value = "deployment")
public class JdbcDeploymentEntity {

    @Id
    private long id;
    private String name;
    private String description;
    private DeploymentStatus status;
    private Long jobId;
    private Long siteId;
    private Instant createdAt;
    private long createdById;

}
