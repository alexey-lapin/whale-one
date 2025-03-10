package com.github.alexeylapin.whaleone.infrastructure.file;

import lombok.SneakyThrows;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class S3FileStorage implements FileStore {

    private final S3Client s3Client;
    private final String bucketName;

    public S3FileStorage(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @SneakyThrows
    @Override
    public FileInfo store(String key,
                          InputStream content,
                          long contentLength,
                          String contentType,
                          Map<String, String> metadata) {
        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType == null ? "application/octet-stream" : contentType)
                .contentLength(contentLength);

        if (metadata != null && !metadata.isEmpty()) {
            requestBuilder.metadata(metadata);
        }

        PutObjectRequest request = requestBuilder.build();
        s3Client.putObject(request, RequestBody.fromInputStream(content, contentLength));

        // Retrieve the object metadata to get the last modified time
        HeadObjectResponse headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());

        return new FileInfo(
                key,
                contentLength,
                contentType,
                metadata != null ? new HashMap<>(metadata) : new HashMap<>(),
                headObjectResponse.lastModified()
        );
    }

    @Override
    public Optional<FileResource> retrieve(String key) {
        try {
            var headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            var getObjectResponse = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            var fileInfo = new FileInfo(
                    key,
                    headObjectResponse.contentLength(),
                    headObjectResponse.contentType(),
                    headObjectResponse.metadata(),
                    headObjectResponse.lastModified()
            );

            return Optional.of(new FileResource(fileInfo, getObjectResponse));
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<FileInfo> list(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream()
                .map(s3Object -> {
                    try {
                        HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                                .bucket(bucketName)
                                .key(s3Object.key())
                                .build());

                        return new FileInfo(
                                s3Object.key(),
                                s3Object.size(),
                                metadata.contentType(),
                                metadata.metadata(),
                                s3Object.lastModified()
                        );
                    } catch (Exception e) {
                        // Log error and return basic info without metadata
                        return new FileInfo(
                                s3Object.key(),
                                s3Object.size(),
                                "application/octet-stream",
                                Collections.emptyMap(),
                                s3Object.lastModified()
                        );
                    }
                })
                .collect(Collectors.toList());
    }

}
