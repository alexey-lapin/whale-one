package com.github.alexeylapin.whaleone.infrastructure.config.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class V002__AdminUserCreateMigration extends BaseJavaMigration {

    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;

    public V002__AdminUserCreateMigration(
            PasswordEncoder passwordEncoder,
            @Value("${whale-one.security.admin.password}") String adminPassword) {
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
    }

    @Override
    public void migrate(Context context) throws Exception {
        Instant now = Instant.now();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));
        jdbcTemplate.update("""
                        INSERT INTO TBL_USER (ID,
                                              VERSION,
                                              CREATED_AT,
                                              CREATED_BY_ID,
                                              LAST_UPDATED_AT,
                                              LAST_UPDATED_BY_ID,
                                              ENABLED,
                                              USERNAME,
                                              PASSWORD)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                0,
                1,
                Timestamp.from(now),
                0,
                Timestamp.from(now),
                0,
                true,
                "admin",
                passwordEncoder.encode(adminPassword));
        jdbcTemplate.update("INSERT INTO TBL_USER_AUTHORITY (USER_ID, NAME) VALUES (?, ?)",
                0,
                "ADMIN");
    }

}
