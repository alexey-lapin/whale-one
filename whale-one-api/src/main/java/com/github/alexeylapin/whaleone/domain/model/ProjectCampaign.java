package com.github.alexeylapin.whaleone.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record ProjectCampaign(
        long id,
        long projectId,
        String name
) {
}
