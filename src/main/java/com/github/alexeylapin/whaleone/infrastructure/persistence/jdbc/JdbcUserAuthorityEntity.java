package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import org.springframework.data.relational.core.mapping.Table;

@Table("tbl_user_authority")
public record JdbcUserAuthorityEntity(String name) {
}
