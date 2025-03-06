package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Table("tbl_user")
public class UserEntity {

    @Id
    private long id;
    @Version
    private int version;
    private Instant createdAt;
    private long createdById;
    private Instant lastUpdatedAt;
    private long lastUpdatedById;

    String username;
    String password;
    boolean enabled;

    @MappedCollection(idColumn = "user_id")
    Set<UserAuthorityEntity> authorities;

}
