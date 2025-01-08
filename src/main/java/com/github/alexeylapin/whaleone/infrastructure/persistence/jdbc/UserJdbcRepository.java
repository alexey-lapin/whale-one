package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserJdbcRepository extends ListCrudRepository<JdbcUserEntity, Long> {

    Optional<JdbcUserEntity> findByUsername(String username);

    Page<JdbcUserEntity> findAllByUsernameNotIn(Collection<String> usernames, Pageable pageable);

}
