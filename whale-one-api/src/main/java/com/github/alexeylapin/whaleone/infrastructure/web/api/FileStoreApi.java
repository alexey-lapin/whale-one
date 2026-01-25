package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.infrastructure.file.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FileStoreApi {

    private static final String ORIGINAL_FILE_NAME = "originalFileName";

    private final FileStore fileStore;

    @PostMapping("/files/{storeName}/{key}")
    public void upload(@PathVariable String storeName,
                       @PathVariable String key,
                       @RequestParam("file") MultipartFile file) throws IOException {
        fileStore.store(key,
                file.getInputStream(),
                file.getSize(),
                file.getContentType(),
                Map.of(ORIGINAL_FILE_NAME, file.getOriginalFilename()));
    }

    @GetMapping("/files/{storeName}/{key}")
    public ResponseEntity<Resource> retrieve(@PathVariable String storeName, @PathVariable String key) {
        var fileResource = fileStore.retrieve(key).orElseThrow();
        var contentDisposition = ContentDisposition.attachment()
                .filename(fileResource.getInfo().getMetadata().get(ORIGINAL_FILE_NAME))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(new InputStreamResource(fileResource.getContent()));
    }

    @DeleteMapping("/files/{storeName}/{key}")
    public void delete(@PathVariable String storeName, @RequestParam("key") String key) {
        fileStore.delete(key);
    }

}
