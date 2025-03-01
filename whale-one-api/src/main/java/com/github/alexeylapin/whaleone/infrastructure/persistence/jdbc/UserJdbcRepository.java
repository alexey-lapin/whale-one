package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserJdbcRepository extends ListCrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findAllByUsernameNotIn(Collection<String> usernames, Pageable pageable);

}
