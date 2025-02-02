package com.github.alexeylapin.whaleone.infrastructure.web;

import com.github.alexeylapin.whaleone.domain.model.DeploymentMarker;
import com.github.alexeylapin.whaleone.domain.repo.DeploymentMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MapController {

    private final DeploymentMetadataRepository deploymentMetadataRepository;

    @GetMapping("/deployments/map")
    public String map(Model model) {
        List<DeploymentMarker> list = deploymentMetadataRepository.findAll(0, 100).getContent().stream()
                .filter(i -> i.latitude() != null && i.longitude() != null)
                .map(i -> new DeploymentMarker(i.id(), i.latitude(), i.longitude(), null))
                .toList();
        model.addAttribute("markers", list);
        return "pages/map";
    }

}
