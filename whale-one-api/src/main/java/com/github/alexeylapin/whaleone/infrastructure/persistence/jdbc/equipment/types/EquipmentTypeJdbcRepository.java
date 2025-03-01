package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.equipment.types;

import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeJdbcRepository extends ListCrudRepository<EquipmentTypeEntity, Long> {

    @Query("""
            SELECT e.*,
                   u.username created_by_name
            FROM equipment_type e
                     JOIN tbl_user u on e.created_by_id = u.id
            WHERE e.id = :id""")
    Optional<EquipmentTypeProjection> findOneById(long id);

    @Query("""
            SELECT e.*,
                   u.username created_by_name
            FROM equipment_type e
                     JOIN tbl_user u on e.created_by_id = u.id
            ORDER BY e.id DESC
            LIMIT :size OFFSET :offset""")
    List<EquipmentTypeProjection> findAll(long size, long offset);

    Page<EquipmentTypeItem> findAllByNameContainingIgnoreCase(@Nullable String name, Pageable pageable);

    @Getter
    @Setter
    class EquipmentTypeProjection extends EquipmentTypeEntity {

        private String createdByName;

    }

}
