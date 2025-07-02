package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;

import java.io.OutputStream;

public interface EquipmentService {

    Page<EquipmentListElement> list(int page, int size, String filter);

    Page<Equipment> search(int page, int size, String filter);

    void export(String filter, OutputStream outputStream);

    Equipment toggleActive(long id, Boolean active, UserRef user);

}
