package com.github.alexeylapin.whaleone.infrastructure.web;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("username", "John Doe");
        return "pages/home";
    }

    // EQUIPMENT
    @GetMapping("/equipment")
    public String equipment(Model model) {
        model.addAttribute("equipmentList", equipmentRepository.findAll());
        model.addAttribute("equipmentTypes", equipmentTypeRepository.findAll().stream()
                .collect(Collectors.toMap(EquipmentType::id, Function.identity())));
        return "pages/equipment";
    }

    @GetMapping("/equipment/new")
    public String equipmentNew(Model model) {
        List<EquipmentType> equipmentTypes = equipmentTypeRepository.findAll();
        model.addAttribute("equipmentTypes", equipmentTypes);
        return "pages/equipment-new";
    }

    @PostMapping("/equipment/new")
    public String equipmentNewSubmit(@ModelAttribute("equipment") EquipmentForm equipmentForm) {
        Equipment equipment = new Equipment(0, equipmentForm.name(), equipmentForm.type(), null);
        equipmentRepository.save(equipment);
        return "redirect:/equipment";
    }

    @GetMapping("/equipment/{id}")
    public String equipmentUpdate(@PathVariable("id") long id, Model model) {
        model.addAttribute("equipment", equipmentRepository.findById(id).orElseThrow());
        model.addAttribute("equipmentTypes", equipmentTypeRepository.findAll().stream()
                .collect(Collectors.toMap(EquipmentType::id, Function.identity())));
        return "pages/equipment-update";
    }

    @PostMapping("/equipment/{id}")
    public String equipmentUpdateSubmit(@PathVariable("id") long id,
                                        @ModelAttribute("equipment") EquipmentForm equipmentForm) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow();
        Equipment alteredEquipment = equipment.toBuilder()
                .name(equipmentForm.name())
                .build();
        equipmentRepository.save(alteredEquipment);
        return "redirect:/equipment";
    }

    @GetMapping("/equipment/types")
    public String equipmentTypes(Model model) {
        model.addAttribute("equipmentTypes", equipmentTypeRepository.findAll());
        return "pages/equipment-types";
    }

    @GetMapping("/equipment/types/new")
    public String equipmentTypeNew() {
        return "pages/equipment-type-new";
    }

    @PostMapping("/equipment/types/new")
    public String equipmentTypeNewSubmit(@ModelAttribute("equipmentType") EquipmentType equipmentType) {
        equipmentTypeRepository.save(equipmentType);
        return "redirect:/equipment/types";
    }

    @GetMapping("/equipment/types/{id}")
    public String equipmentTypeUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("equipmentType", equipmentTypeRepository.findById(id).orElseThrow());
        return "pages/equipment-type-update";
    }

    @PostMapping("/equipment/types/{id}")
    public String equipmentTypeUpdateSubmit(@PathVariable("id") int id,
                                            @ModelAttribute("equipmentType") EquipmentType formEquipmentType) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id).orElseThrow();
        equipmentTypeRepository.save(equipmentType.toBuilder()
                .name(formEquipmentType.name())
                .build());
        return "redirect:/equipment/types";
    }

    @PostMapping("/equipment/types/{equipmentTypeId}/attributes")
    public String equipmentTypeUpdateAttributeNewSubmit(
            @PathVariable("equipmentTypeId") int equipmentTypeId,
            @ModelAttribute("equipmentTypeAttribute") EquipmentTypeAttribute attribute) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(equipmentTypeId).orElseThrow();
        equipmentType.attributes().add(attribute);
        equipmentTypeRepository.save(equipmentType);
        return "redirect:/equipment/types/" + equipmentTypeId;
    }

    @PostMapping("/equipment/types/{equipmentTypeId}/attributes2")
    public String equipmentTypeUpdateAttributeSort(
            @PathVariable("equipmentTypeId") int equipmentTypeId,
            @RequestParam("id") List<Long> attributes,
            Model model) {
        System.out.println(attributes);
        EquipmentType equipmentType = equipmentTypeRepository.findById(equipmentTypeId).orElseThrow();
        Map<Long, EquipmentTypeAttribute> map = equipmentType.attributes().stream()
                .collect(Collectors.toMap(EquipmentTypeAttribute::id, Function.identity()));
        equipmentType.attributes().clear();
        for (Long attribute : attributes) {
            equipmentType.attributes().add(map.get(attribute));
        }
        equipmentTypeRepository.save(equipmentType);
        model.addAttribute("equipmentType", equipmentType);
        return "partials/equipment-type-attributes";
    }

    @DeleteMapping("/equipment/types/{equipmentTypeId}/attributes/{attributeId}")
    public String equipmentTypeUpdateAttributeDelete(
            @PathVariable("equipmentTypeId") int equipmentTypeId,
            @PathVariable("attributeId") int attributeId,
            Model model) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(equipmentTypeId).orElseThrow();
        equipmentType.attributes().removeIf(it -> it.id() == attributeId);
        equipmentTypeRepository.save(equipmentType);
        model.addAttribute("equipmentType", equipmentType);
        return "partials/equipment-type-attributes";
    }

    public record EquipmentForm(
//            Long deploymentId,
            String name,
            Integer type
    ) {
    }

    public record EquipmentTypeAttributeItems(List<EquipmentTypeAttribute> items) {
    }

}
