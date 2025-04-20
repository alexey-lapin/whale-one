package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;

public interface EquipmentTypeService {

    Page<EquipmentType> list(int page, int size, String filter);

}
