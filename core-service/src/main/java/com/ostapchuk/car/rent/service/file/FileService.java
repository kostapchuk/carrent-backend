package com.ostapchuk.car.rent.service.file;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FileService {

    @Async
    CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile);
}
