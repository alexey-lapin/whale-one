package com.github.alexeylapin.whaleone.infrastructure.web;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;


    // EQUIPMENT

    @GetMapping("/equipment")
    public String equipment(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model) {
        model.addAttribute("equipmentList", equipmentRepository.findAll(page, size));
        model.addAttribute("equipmentTypes", getTypeMap());
        return "pages/equipment";
    }

    @GetMapping("/equipment/new")
    public String equipmentNew(Model model) {
        List<EquipmentType> equipmentTypes = equipmentTypeRepository.findAll(0, 100).getContent();
        model.addAttribute("equipmentTypes", equipmentTypes);
        return "pages/equipment-new";
    }

    @PostMapping("/equipment/new")
    public String equipmentNewSubmit(@ModelAttribute("equipment") EquipmentForm equipmentForm,
                                     @AuthenticationPrincipal IdUser user) {
        Equipment equipment = new Equipment(0,
                0,
                ZonedDateTime.now(),
                user.getId(),
                user.getUsername(),
                equipmentForm.name(),
                equipmentForm.type(),
                null);
        equipmentRepository.save(equipment);
        return "redirect:/equipment";
    }

    @GetMapping("/equipment/{id}")
    public String equipmentUpdate(@PathVariable("id") long id, Model model) {
        model.addAttribute("equipment", equipmentRepository.findById(id).orElseThrow());
        model.addAttribute("equipmentTypes", getTypeMap());
        return "pages/equipment-update";
    }

    @PostMapping("/equipment/{id}")
    public String equipmentUpdateSubmit(@PathVariable("id") long id,
                                        @ModelAttribute("equipment") EquipmentForm equipmentForm) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow();
        Equipment alteredEquipment = equipment.toBuilder()
                .version(equipmentForm.version())
                .name(equipmentForm.name())
                .build();
        equipmentRepository.save(alteredEquipment);
        return "redirect:/equipment";
    }


    // EQUIPMENT TYPES

    @GetMapping("/equipment/types")
    public String equipmentTypes(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Model model) {
        model.addAttribute("equipmentTypes", equipmentTypeRepository.findAll(page, size));
        return "pages/equipment-types";
    }

    @GetMapping("/equipment/types/new")
    public String equipmentTypeNew() {
        return "pages/equipment-type-new";
    }

    @PostMapping("/equipment/types/new")
    public String equipmentTypeNewSubmit(@ModelAttribute("equipmentType") EquipmentType equipmentType,
                                         @AuthenticationPrincipal IdUser user) {
        equipmentType = equipmentType.toBuilder()
                .id(0)
                .version(0)
                .createdAt(ZonedDateTime.now())
                .createdById(user.getId())
                .createdBy(user.getUsername())
                .attributes(List.of())
                .build();
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
                .version(formEquipmentType.version())
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

    private Map<Long, EquipmentType> getTypeMap() {
        return equipmentTypeRepository.findAll(0, 100).getContent().stream()
                .collect(Collectors.toMap(EquipmentType::id, Function.identity()));
    }

    public record EquipmentForm(
            int version,
            String name,
            Integer type
    ) {
    }

}
