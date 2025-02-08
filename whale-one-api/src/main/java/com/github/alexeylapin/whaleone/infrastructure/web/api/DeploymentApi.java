package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DeploymentApi {

    private final DeploymentRepository deploymentRepository;

    @PostMapping("/deployments")
    public Deployment create(@RequestBody Deployment deployment,
                             @AuthenticationPrincipal IdUser user) {
        deployment = deployment.toBuilder()
                .id(0)
                .version(0)
                .createdAt(ZonedDateTime.now())
                .createdById(user.getId())
                .createdBy(user.getUsername())
                .build();
        return deploymentRepository.save(deployment);
    }

}
