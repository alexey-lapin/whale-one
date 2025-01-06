package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder(toBuilder = true)
public record DeploymentMetadata(
        long id,
        int version,
        Double latitude,
        Double longitude,
        Integer sampleRate,
        Integer dutyCycleRecord,
        Integer dutyCycleSleep,
        Integer dutyCycleInterval,
        ZonedDateTime startedAt,
        ZonedDateTime stoppedAt,
        String recordingStatus,
        String recordingStatusNote
) {
}
