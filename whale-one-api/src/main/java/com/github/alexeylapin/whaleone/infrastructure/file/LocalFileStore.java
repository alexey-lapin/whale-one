package com.github.alexeylapin.whaleone.infrastructure.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalFileStore implements FileStore {

    private final Path basePath;
    private final ObjectMapper objectMapper;

    public LocalFileStore(String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();

        // Create base directory if it doesn't exist
        try {
            Files.createDirectories(this.basePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public FileInfo store(String key, InputStream content, long contentLength, String contentType, Map<String, String> metadata) {
        Path filePath = resolveFilePath(key);
        Path metadataPath = resolveMetadataPath(key);

        try {
            // Create parent directories if they don't exist
            Files.createDirectories(filePath.getParent());

            // Copy the content to the file
            Files.copy(content, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save metadata to a separate file
            LocalFileMetadata fileMetadata = new LocalFileMetadata(
                    contentType,
                    metadata != null ? new HashMap<>(metadata) : new HashMap<>(),
                    Instant.now()
            );

            objectMapper.writeValue(metadataPath.toFile(), fileMetadata);

            return new FileInfo(
                    key,
                    Files.size(filePath),
                    contentType,
                    metadata != null ? new HashMap<>(metadata) : new HashMap<>(),
                    fileMetadata.getLastModified()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + key, e);
        }
    }

    @Override
    public Optional<FileResource> retrieve(String key) {
        Path filePath = resolveFilePath(key);
        Path metadataPath = resolveMetadataPath(key);

        if (!Files.exists(filePath) || !Files.exists(metadataPath)) {
            return Optional.empty();
        }

        try {
            LocalFileMetadata metadata = objectMapper.readValue(metadataPath.toFile(), LocalFileMetadata.class);

            FileInfo fileInfo = new FileInfo(
                    key,
                    Files.size(filePath),
                    metadata.getContentType(),
                    metadata.getMetadata(),
                    metadata.getLastModified()
            );

            InputStream inputStream = Files.newInputStream(filePath);
            return Optional.of(new FileResource(fileInfo, inputStream));
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve file: " + key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        Path filePath = resolveFilePath(key);
        Path metadataPath = resolveMetadataPath(key);
        return Files.exists(filePath) && Files.exists(metadataPath);
    }

    @Override
    public boolean delete(String key) {
        Path filePath = resolveFilePath(key);
        Path metadataPath = resolveMetadataPath(key);

        boolean success = true;

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            success = false;
        }

        try {
            if (Files.exists(metadataPath)) {
                Files.delete(metadataPath);
            }
        } catch (IOException e) {
            success = false;
        }

        return success;
    }

    @Override
    public List<FileInfo> list(String prefix) {
        Path prefixPath = resolveDirectoryPath(prefix);

        if (!Files.exists(prefixPath) || !Files.isDirectory(prefixPath)) {
            return Collections.emptyList();
        }

        try {
            return Files.walk(prefixPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toString().endsWith(".metadata"))
                    .map(path -> {
                        try {
                            // Convert filesystem path to storage key
                            String key = basePath.relativize(path).toString().replace(File.separatorChar, '/');
                            Path metadataPath = resolveMetadataPath(key);

                            if (Files.exists(metadataPath)) {
                                LocalFileMetadata metadata = objectMapper.readValue(metadataPath.toFile(), LocalFileMetadata.class);

                                return new FileInfo(
                                        key,
                                        Files.size(path),
                                        metadata.getContentType(),
                                        metadata.getMetadata(),
                                        metadata.getLastModified()
                                );
                            } else {
                                // Fallback if metadata is missing
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

                                return new FileInfo(
                                        key,
                                        Files.size(path),
                                        "application/octet-stream",
                                        Collections.emptyMap(),
                                        attrs.lastModifiedTime().toInstant()
                                );
                            }
                        } catch (IOException e) {
                            // Log error and skip this file
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files with prefix: " + prefix, e);
        }
    }

    // Helper methods

    private Path resolveFilePath(String key) {
        // Normalize the key to avoid directory traversal attacks
        Path normalized = Paths.get(key).normalize();
        return basePath.resolve(normalized);
    }

    private Path resolveMetadataPath(String key) {
        Path normalized = Paths.get(key + ".metadata").normalize();
        return basePath.resolve(normalized);
    }

    private Path resolveDirectoryPath(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return basePath;
        }
        Path normalized = Paths.get(prefix).normalize();
        return basePath.resolve(normalized);
    }

    // Metadata storage class

    @Setter
    @Getter
    static class LocalFileMetadata {
        private String contentType;
        private Map<String, String> metadata;
        private Instant lastModified;

        // Default constructor for Jackson
        public LocalFileMetadata() {
        }

        public LocalFileMetadata(String contentType, Map<String, String> metadata, Instant lastModified) {
            this.contentType = contentType;
            this.metadata = metadata;
            this.lastModified = lastModified;
        }

    }
}
