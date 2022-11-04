package com.ostapchuk.car.rent.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Primary
@Component
@RequiredArgsConstructor
public class CloudinaryFileService implements FileService {

    private final Cloudinary cloudinary;

    @Async
    @SneakyThrows
    @Override
    public CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile) {
        final File file = convertMultiPartFileToFile(multipartFile);
        final Object url =  cloudinary.uploader().upload(file,
                ObjectUtils.asMap("public_id", UUID.randomUUID().toString())
        ).get("url");
        Files.delete(file.toPath());
        return CompletableFuture.completedFuture(Optional.ofNullable(url.toString()));
    }
}
