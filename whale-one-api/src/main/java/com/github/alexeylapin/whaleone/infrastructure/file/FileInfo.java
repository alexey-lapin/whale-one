package com.github.alexeylapin.whaleone.infrastructure.file;

import java.time.Instant;
import java.util.Map;

public class FileInfo {

    private final String key;
    private final long size;
    private final String contentType;
    private final Map<String, String> metadata;
    private final Instant lastModified;

    public FileInfo(String key, long size, String contentType, Map<String, String> metadata, Instant lastModified) {
        this.key = key;
        this.size = size;
        this.contentType = contentType;
        this.metadata = metadata;
        this.lastModified = lastModified;
    }

    public String getKey() {
        return key;
    }

    public long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Instant getLastModified() {
        return lastModified;
    }

}
