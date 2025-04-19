package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.Page;

public interface EquipmentService {

    Page<EquipmentListElement> list(int page, int size, String filter);

}
