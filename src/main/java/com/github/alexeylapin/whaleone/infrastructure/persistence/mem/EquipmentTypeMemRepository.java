package com.github.alexeylapin.whaleone.infrastructure.persistence.mem;

import com.github.alexeylapin.whaleone.domain.model.EquipmentType;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentTypeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//@Repository
public class EquipmentTypeMemRepository implements EquipmentTypeRepository {

    private final AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Integer, EquipmentType> storage = new ConcurrentHashMap<>();

    @Override
    public List<EquipmentType> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Optional<EquipmentType> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public EquipmentType save(EquipmentType equipmentType) {
        if (equipmentType.id() == 0) {
            equipmentType = equipmentType.toBuilder().id(idGenerator.incrementAndGet()).build();
        }
        storage.put(equipmentType.id(), equipmentType);
        return equipmentType;
    }

}
