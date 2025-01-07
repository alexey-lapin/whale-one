package com.github.alexeylapin.whaleone.infrastructure.web;

import com.github.alexeylapin.whaleone.domain.model.Deployment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment;
import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata;
import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentEquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentMetadataRepository;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RegisterReflectionForBinding(DeploymentController.NewDeployment.class)
@Controller
public class DeploymentController {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentMetadataRepository deploymentMetadataRepository;
    private final DeploymentEquipmentRepository deploymentEquipmentRepository;
    private final EquipmentRepository equipmentRepository;

    @GetMapping("/deployments")
    public String deployments(Model model) {
        List<Deployment> deployments = new ArrayList<>(deploymentRepository.findAll());
        deployments.sort((a, b) -> Long.compare(b.id(), a.id()));
        model.addAttribute("deployments", deployments);
        return "pages/deployments";
    }

    @GetMapping("/deployments/new")
    public String deploymentNew() {
        return "pages/deployment-new";
    }

    @PostMapping("/deployments/new")
    public String deploymentNewSubmit(@Valid NewDeployment newDeployment,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "pages/deployment-new";
        }
        Deployment deployment = Deployment.builder()
                .name(newDeployment.name())
                .description(newDeployment.description())
                .status(DeploymentStatus.NEW)
                .createdAt(ZonedDateTime.now())
                .createdBy("admin")
                .build();
        deploymentRepository.save(deployment);
        return "redirect:/deployments";
    }

    @GetMapping("/deployments/{id}")
    public String deploymentsUpdate(@PathVariable("id") long id, Model model) {
        Deployment deployment = deploymentRepository.findById(id).orElseThrow();
        model.addAttribute("deployment", deployment);

        List<DeploymentEquipment> deploymentEquipmentList = deploymentEquipmentRepository.findAllByDeploymentId(id);
        model.addAttribute("deploymentEquipmentList", deploymentEquipmentList);

        model.addAttribute("equipment", equipmentRepository.findAll().stream()
                .collect(Collectors.toMap(Equipment::id, Function.identity())));

        model.addAttribute("isEquipmentEditable", deployment.status() == DeploymentStatus.ASSIGN);

        return "pages/deployment-update";
    }

    @Transactional
    @PostMapping("/deployments/{id}")
    public String deploymentsUpdateSubmit(@PathVariable("id") long id,
                                          @RequestParam(value = "status") Optional<DeploymentStatus> status,
                                          @ModelAttribute("deployment") Deployment formDeployment) {
        Deployment deployment = deploymentRepository.findById(id).orElseThrow();
        Deployment alteredDeployment = deployment.toBuilder()
                .name(formDeployment.name())
                .description(formDeployment.description())
                .status(status.orElse(deployment.status()))
                .build();
        deploymentRepository.save(alteredDeployment);
        if (alteredDeployment.status().isTerminal()) {
            equipmentRepository.findAllByDeploymentId(id).forEach(equipment ->
                    equipmentRepository.save(equipment.toBuilder()
                            .deploymentId(null)
                            .build()));
        }
        return "redirect:/deployments/" + id;
    }

    @Transactional
    @PostMapping("/deployments/{id}/equipment")
    public String deploymentEquipmentSubmit(@PathVariable("id") long id,
                                            @ModelAttribute("equipment-id") Long equipmentId) {
        deploymentEquipmentRepository.save(new DeploymentEquipment(id, equipmentId));
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        equipmentRepository.save(equipment.toBuilder()
                .deploymentId(id)
                .build());
        return "redirect:/deployments/" + id;
    }

    @Transactional
    @DeleteMapping("/deployments/{id}/equipment/{equipmentId}")
    public String deploymentEquipmentDeleteSubmit(@PathVariable("id") long id,
                                                  @PathVariable("equipmentId") long equipmentId,
                                                  Model model) {
        Deployment deployment = deploymentRepository.findById(id).orElseThrow();
        deploymentEquipmentRepository.delete(new DeploymentEquipment(id, equipmentId));

        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow();
        equipmentRepository.save(equipment.toBuilder()
                .deploymentId(null)
                .build());

        model.addAttribute("deployment", deployment);
        model.addAttribute("deploymentEquipmentList", deploymentEquipmentRepository.findAllByDeploymentId(id));
        model.addAttribute("equipmentMap", equipmentRepository.findAll().stream()
                .collect(Collectors.toMap(Equipment::id, Function.identity())));
        model.addAttribute("isEquipmentEditable", deployment.status() == DeploymentStatus.ASSIGN);
        return "partials/deployment-equipment";
    }

    @GetMapping("/deployments/{id}/metadata")
    public String deploymentMetadata(@PathVariable("id") long id, Model model) {
        DeploymentMetadata deploymentMetadata = deploymentMetadataRepository.findById(id)
                .orElse(DeploymentMetadata.builder().id(id).build());
        model.addAttribute("deploymentMetadata", deploymentMetadata);
        return "pages/deployment-metadata";
    }

    @PostMapping("/deployments/{id}/metadata")
    public String deploymentMetadataSubmit(
            @PathVariable("id") long id,
            @ModelAttribute("deploymentMetadata") DeploymentMetadata formDeploymentMetadata,
            Model model) {
        DeploymentMetadata deploymentMetadata = deploymentMetadataRepository.findById(id)
                .orElse(DeploymentMetadata.builder().id(id).build());
        if (!formDeploymentMetadata.equals(deploymentMetadata)) {
            deploymentMetadata = deploymentMetadata.toBuilder()
                    .version(formDeploymentMetadata.version())
                    .latitude(formDeploymentMetadata.latitude())
                    .longitude(formDeploymentMetadata.longitude())
                    .sampleRate(formDeploymentMetadata.sampleRate())
                    .dutyCycleSleep(formDeploymentMetadata.dutyCycleSleep())
                    .dutyCycleInterval(formDeploymentMetadata.dutyCycleInterval())
                    .dutyCycleRecord(formDeploymentMetadata.dutyCycleRecord())
                    .recordingStatus(formDeploymentMetadata.recordingStatus())
                    .recordingStatusNote(formDeploymentMetadata.recordingStatusNote())
                    .build();
            deploymentMetadata = deploymentMetadataRepository.save(formDeploymentMetadata);
        }
        model.addAttribute("deploymentMetadata", deploymentMetadata);
        return "pages/deployment-metadata";
    }

    public record NewDeployment(@NotBlank String name, String description) {
    }

}
