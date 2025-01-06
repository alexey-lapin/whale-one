package com.github.alexeylapin.whaleone.infrastructure.persistence.mem;

import com.github.alexeylapin.whaleone.domain.model.Equipment;
import com.github.alexeylapin.whaleone.domain.repo.EquipmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//@Repository
public class EquipmentMemRepository implements EquipmentRepository {

    private final AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Long, Equipment> storage = new ConcurrentHashMap<>();

    @Override
    public List<Equipment> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Equipment> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Equipment save(Equipment equipment) {
        if (equipment.id() == 0) {
            equipment = equipment.toBuilder().id(idGenerator.incrementAndGet()).build();
        }
        storage.put(equipment.id(), equipment);
        return equipment;
    }

    @Override
    public List<Equipment> findAllUnassigned() {
        return List.of();
    }

    @Override
    public List<Equipment> findAllByDeploymentId(long id) {
        return List.of();
    }
}
