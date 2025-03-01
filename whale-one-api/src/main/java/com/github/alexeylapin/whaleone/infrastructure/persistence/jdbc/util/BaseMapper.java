package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper
public interface BaseMapper {

    default ZonedDateTime map(Instant source) {
        if (source == null) {
            return null;
        }
        return source.atZone(ZoneId.systemDefault());
    }

    default Instant map(ZonedDateTime source) {
        if (source == null) {
            return null;
        }
        return source.toInstant();
    }

}
