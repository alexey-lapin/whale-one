package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@Table(value = "project")
public class ProjectEntity {

    @Id
    private long id;
    @Version
    private int version;
    private Instant createdAt;
    private long createdById;
    private Instant lastUpdatedAt;
    private long lastUpdatedById;

    private String name;
    private String client;
    private String ownership;
    private String region;
    private String type;
    private String goal;
    private String description;

}
