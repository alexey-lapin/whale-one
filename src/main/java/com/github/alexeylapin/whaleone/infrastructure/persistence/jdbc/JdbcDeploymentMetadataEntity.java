package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.ZonedDateTime;

@Table("deployment_metadata")
public record JdbcDeploymentMetadataEntity(
        @Id
        long deploymentId,
        @Version
        int version,
        Double latitude,
        Double longitude,
        Integer sampleRate,
        Integer dutyCycleRecord,
        Integer dutyCycleSleep,
        Integer dutyCycleInterval,
        Instant startedAt,
        Instant stoppedAt,
        String recordingStatus,
        String recordingStatusNote
) {
}
