package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.model.EquipmentItem;
import com.github.alexeylapin.whaleone.domain.model.EquipmentListElement;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(long id);

    Page<EquipmentListElement> list(int page, int size, QuerySpec querySpec);

    Page<Equipment> search(int page, int size, Collection<EquipmentTypeAttribute> equipmentTypeAttributes, QuerySpec querySpec);

    long count(QuerySpec querySpec);

    long count(Collection<EquipmentTypeAttribute> equipmentTypeAttributes, QuerySpec querySpec);

    Optional<EquipmentItem> findItemById(long id);

    List<EquipmentItem> findAllItems(long typeId, String name, boolean includeAllocated);

    List<Equipment> findAllByDeploymentId(long id);

    void deleteById(long id);

    void updateDeploymentId(long id, Long deploymentId);

}
