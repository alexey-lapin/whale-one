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
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM equipment_type e
                     JOIN tbl_user u1 on e.created_by_id = u1.id
                     JOIN tbl_user u2 on e.last_updated_by_id = u2.id
            WHERE e.id = :id""")
    Optional<EquipmentTypeProjection> findOneById(long id);

    @Query("""
            SELECT e.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM equipment_type e
                     JOIN tbl_user u1 on e.created_by_id = u1.id
                     JOIN tbl_user u2 on e.last_updated_by_id = u2.id
            ORDER BY e.id DESC
            LIMIT :size OFFSET :offset""")
    List<EquipmentTypeProjection> findAll(long size, long offset);

    Page<EquipmentTypeItem> findAllByNameContainingIgnoreCase(@Nullable String name, Pageable pageable);

    @Getter
    @Setter
    class EquipmentTypeProjection extends EquipmentTypeEntity {

        private String createdByName;
        private String lastUpdatedByName;

    }

}
