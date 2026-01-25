package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisTypeJdbcRepository extends ListCrudRepository<AnalysisTypeEntity, Long> {

    @Query("""
            SELECT at.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM analysis_type at
                     JOIN tbl_user u1 on at.created_by_id = u1.id
                     JOIN tbl_user u2 on at.last_updated_by_id = u2.id
            WHERE at.id = :id""")
    Optional<AnalysisTypeProjection> findOneById(long id);

    @Query("""
            SELECT at.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM analysis_type at
                     JOIN tbl_user u1 on at.created_by_id = u1.id
                     JOIN tbl_user u2 on at.last_updated_by_id = u2.id
            ORDER BY at.id DESC
            LIMIT :size OFFSET :offset""")
    List<AnalysisTypeProjection> findAll(long size, long offset);

    @Getter
    @Setter
    class AnalysisTypeProjection extends AnalysisTypeEntity {

        private String createdByName;
        private String lastUpdatedByName;

    }

}
