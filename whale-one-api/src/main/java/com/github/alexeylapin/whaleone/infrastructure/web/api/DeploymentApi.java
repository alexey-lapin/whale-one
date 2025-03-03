package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api")
public class DeploymentApi {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentEquipmentRepository deploymentEquipmentRepository;
    private final EquipmentRepository equipmentRepository;

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
                .build();
        return deploymentRepository.save(alteredDeployment);
    }

    // Deployment Equipment

    @Transactional
    @PostMapping("/deployments/{deploymentId}/equipment")
    public void deploymentEquipmentAdd(@PathVariable long deploymentId,
                                       @RequestBody DeploymentEquipment deploymentEquipment) {
        deploymentEquipmentRepository.save(deploymentEquipment);
        var equipment = equipmentRepository.findById(deploymentEquipment.equipmentId()).orElseThrow();
        if (equipment.deploymentId() != null) {
            throw new RuntimeException("Equipment is already deployed");
        }
        equipmentRepository.save(equipment.toBuilder()
                .deploymentId(deploymentId)
                .build());
    }

    @GetMapping("/deployments/{deploymentId}/equipment")
    public List<DeploymentEquipmentItem> deploymentEquipmentList(@PathVariable long deploymentId) {
        return deploymentEquipmentRepository.findAllByDeploymentId(deploymentId);
    }

    @Transactional
    @DeleteMapping("/deployments/{deploymentId}/equipment/{equipmentId}")
    public void deploymentEquipmentDelete(@PathVariable long deploymentId,
                                          @PathVariable long equipmentId) {
        deploymentEquipmentRepository.delete(new DeploymentEquipment(deploymentId, equipmentId));
        var equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        if (equipment.deploymentId() != deploymentId) {
            throw new RuntimeException("Deployment Equipment mismatch");
        }
        equipmentRepository.save(equipment.toBuilder()
                .deploymentId(null)
                .build());
    }

}
