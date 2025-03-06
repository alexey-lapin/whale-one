package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.UserListElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserJdbcRepository extends ListCrudRepository<UserEntity, Long> {

    @Query("""
            SELECT u.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM tbl_user u
                     JOIN tbl_user u1 on u.created_by_id = u1.id
                     JOIN tbl_user u2 on u.last_updated_by_id = u2.id
            WHERE u.id = :id
            """)
    Optional<UserProjection> findOneById(long id);

    @Query("""
            SELECT u.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM tbl_user u
                     JOIN tbl_user u1 on u.created_by_id = u1.id
                     JOIN tbl_user u2 on u.last_updated_by_id = u2.id
            WHERE u.username = :username
            """)
    Optional<UserProjection> findByUsername(String username);

    @Query("""
            SELECT u.*,
                   u1.username created_by_name,
                   u2.username last_updated_by_name
            FROM tbl_user u
                     JOIN tbl_user u1 on u.created_by_id = u1.id
                     JOIN tbl_user u2 on u.last_updated_by_id = u2.id
            WHERE u.username NOT IN (:usernames)
            ORDER BY u.username ASC
            LIMIT :size OFFSET :offset""")
    List<UserListElement> findAllByUsernameNotIn(Collection<String> usernames, int size, long offset);

    @Getter
    @Setter
    class UserProjection extends UserEntity {

        private String createdByName;
        private String lastUpdatedByName;

    }

}
