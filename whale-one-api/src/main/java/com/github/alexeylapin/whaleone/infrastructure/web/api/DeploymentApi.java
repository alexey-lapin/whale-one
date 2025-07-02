package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.application.service.DeploymentService;
import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentElement;
import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api")
public class DeploymentApi {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentEquipmentRepository deploymentEquipmentRepository;
    private final DeploymentService deploymentService;

    @PostMapping("/deployments")
    public Deployment create(@RequestBody Deployment deployment,
                             @AuthenticationPrincipal IdUser user) {
        var now = ZonedDateTime.now();
        var userRef = new UserRef(user.getId(), user.getName());
        deployment = deployment.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .status(DeploymentStatus.NEW)
                .build();
        return deploymentRepository.save(deployment);
    }

    @PutMapping("/deployments/{id}")
    public Deployment update(@PathVariable long id,
                             @RequestBody Deployment deployment,
                             @AuthenticationPrincipal IdUser user) {
        deployment = deployment.toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        return deploymentRepository.save(deployment);
    }

    @GetMapping("/deployments/{id}")
    public Deployment get(@PathVariable long id) {
        return deploymentRepository.findById(id).orElseThrow();
    }

    @GetMapping("/deployments")
    public PageDto<Deployment> getAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam Optional<String> name,
                                      @RequestParam Optional<Long> projectId,
                                      @RequestParam Optional<Long> projectSiteId,
                                      @RequestParam Optional<DeploymentStatus> status) {
        var aPage = deploymentRepository.findAll(page,
                size,
                name.orElse(null),
                projectId.orElse(null),
                projectSiteId.orElse(null),
                status.map(Enum::name).orElse(null));
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    @PutMapping("/deployments/{deploymentId}/status")
    public Deployment create(@PathVariable long deploymentId,
                             @RequestParam DeploymentStatus status,
                             @AuthenticationPrincipal IdUser user) {
        var deployment = deploymentRepository.findById(deploymentId).orElseThrow();
        var alteredDeployment = deployment.toBuilder()
                .status(status)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        return deploymentRepository.save(alteredDeployment);
    }

    // Deployment Equipment

    public record CreateDeploymentEquipment(
            @NotNull @Min(1) Long deploymentId,
            @NotNull @Min(1) Long equipmentId) {
    }

    @Transactional
    @PostMapping("/deployments/{deploymentId}/equipment")
    public void deploymentEquipmentAdd(@PathVariable long deploymentId,
                                       @Valid @RequestBody CreateDeploymentEquipment deploymentEquipment,
                                       @AuthenticationPrincipal IdUser user) {
        deploymentService.addEquipment(deploymentEquipment.deploymentId(),
                deploymentEquipment.equipmentId(),
                new UserRef(user.getId(), user.getName()));
    }

    @Transactional
    @DeleteMapping("/deployments/{deploymentId}/equipment/{equipmentId}")
    public void deploymentEquipmentDelete(@PathVariable long deploymentId,
                                          @PathVariable long equipmentId,
                                          @AuthenticationPrincipal IdUser user) {
        deploymentService.deleteEquipment(deploymentId,
                equipmentId,
                new UserRef(user.getId(), user.getName()));
    }

    @GetMapping("/deployments/{deploymentId}/equipment/elements")
    public List<DeploymentEquipmentElement> deploymentEquipmentElements(@PathVariable long deploymentId) {
        return deploymentEquipmentRepository.findAllElementsByDeploymentId(deploymentId);
    }

}
