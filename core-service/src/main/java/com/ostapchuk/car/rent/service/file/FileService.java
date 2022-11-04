package com.ostapchuk.car.rent.service.file;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FileService {

    CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile);

    @SneakyThrows
    default File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        return file;
    }
}
