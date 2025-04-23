package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeItem;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeRepository {

    EquipmentType save(EquipmentType equipmentType);

    Optional<EquipmentType> findById(long id);

    Page<EquipmentType> findAll(int page, int size);

    Page<EquipmentType> list(int page, int size, QuerySpec querySpec);

    long count(QuerySpec querySpec);

    List<EquipmentTypeItem> findAllItems(String nameQuery);

    void deleteById(long id);

}
