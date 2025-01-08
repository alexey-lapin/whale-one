package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("tbl_user")
public record JdbcUserEntity(
        @Id
        long id,
        @Version
        int version,
        String username,
        String password,
        boolean enabled,
        @MappedCollection(idColumn = "user_id")
        Set<JdbcUserAuthorityEntity> authorities
) {
}
