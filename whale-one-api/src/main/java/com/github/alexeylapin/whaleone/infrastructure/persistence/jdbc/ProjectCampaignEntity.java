package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "project_campaign")
public class ProjectCampaignEntity {

    @Id
    private long id;
    private long projectId;
    private String name;

}
