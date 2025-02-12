package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.JsonValue;
import lombok.SneakyThrows;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Override
    protected List<?> userConverters() {
        return List.of(new StringReadingConverter(), new StringWritingConverter(), new TimestampToZonedDateTimeConverter());
    }


//    @Component
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

//    @Component
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

}
