package com.github.alexeylapin.whaleone.infrastructure.file;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FileStore {

    /**
     * Stores a file with metadata.
     *
     * @param key           Identifier for the file in the storage system
     * @param content       InputStream with the file content
     * @param contentLength Length of the content in bytes
     * @param contentType   MIME type of the content
     * @param metadata      Additional metadata to store with the file
     * @return Information about the stored file
     */
    FileInfo store(String key, InputStream content, long contentLength, String contentType, Map<String, String> metadata);

    /**
     * Retrieves a file.
     *
     * @param key Identifier of the file to retrieve
     * @return Optional containing file information if found, empty otherwise
     */
    Optional<FileResource> retrieve(String key);

    /**
     * Checks if a file exists.
     *
     * @param key Identifier to check
     * @return true if the file exists, false otherwise
     */
    boolean exists(String key);

    /**
     * Deletes a file.
     *
     * @param key Identifier of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    boolean delete(String key);

    /**
     * Lists all files with a common prefix.
     *
     * @param prefix Prefix to filter files by
     * @return List of file information
     */
    List<FileInfo> list(String prefix);

}
