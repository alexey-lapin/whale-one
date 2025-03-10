package com.github.alexeylapin.whaleone.infrastructure.file;

import java.io.InputStream;

/**
 * Resource containing file content and information.
 */
public class FileResource implements AutoCloseable {

    private final FileInfo info;
    private final InputStream content;

    public FileResource(FileInfo info, InputStream content) {
        this.info = info;
        this.content = content;
    }

    public FileInfo getInfo() {
        return info;
    }

    public InputStream getContent() {
        return content;
    }

    /**
     * Closes resources associated with this file resource.
     */
    @Override
    public void close() {
        try {
            if (content != null) {
                content.close();
            }
        } catch (Exception e) {
            // Log error or handle accordingly
        }
    }

}
