package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.UserRef;

public interface EquipmentService {

    Page<EquipmentListElement> list(int page, int size, String filter);

    Equipment toggleActive(long id, UserRef user);

}
