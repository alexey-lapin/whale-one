package com.github.alexeylapin.whaleone.domain.model;

import lombok.Getter;

@Getter
public enum DeploymentStatus {

    NEW(false),
    ASSIGN(false),
    DEPLOYED(false),
    RECOVERED(true),
    CANCELLED(true),
    ;

    private final boolean terminal;

    DeploymentStatus(boolean terminal) {
        this.terminal = terminal;
    }

}
