package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.JsonValue;
import lombok.SneakyThrows;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    public JdbcDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

    @Override
    protected List<?> userConverters() {
        return List.of(
                new StringReadingConverter(),
                new StringWritingConverter(),
                new TimestampToZonedDateTimeConverter(),
                new ZonedDateTimeToTimestampConverter()
        );
    }

    @WritingConverter
    static class StringWritingConverter implements Converter<JsonValue, PGobject> {

        @SneakyThrows
        @Override
        public PGobject convert(JsonValue source) {
            var jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(source.getValue());
            return jsonObject;
        }

    }

    @ReadingConverter
    static class StringReadingConverter implements Converter<PGobject, JsonValue> {

        @Override
        public JsonValue convert(PGobject pgObject) {
            return new JsonValue(pgObject.getValue());
        }

    }

    @ReadingConverter
    static class TimestampToZonedDateTimeConverter implements Converter<Timestamp, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(Timestamp source) {
            return source.toInstant().atZone(ZoneId.systemDefault());
        }

    }

    @WritingConverter
    static class ZonedDateTimeToTimestampConverter implements Converter<ZonedDateTime, Timestamp> {

        @Override
        public Timestamp convert(ZonedDateTime source) {
            return Timestamp.from(source.toInstant());
        }

    }

}
