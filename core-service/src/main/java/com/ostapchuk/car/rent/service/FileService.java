package com.ostapchuk.car.rent.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ostapchuk.car.rent.util.Constant.UNDERSCORE;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final AmazonS3Client amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Async
    public CompletableFuture<Optional<String>> uploadToS3(final MultipartFile multipartFile) {
        Optional<String> fileNameOpt = Optional.empty();
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            final String fileName = LocalDateTime.now() + UNDERSCORE + UUID.randomUUID();
            final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, file);
            amazonS3.putObject(putObjectRequest);
            log.info("Uploading file with name {}", fileName);
            Files.delete(file.toPath()); // Remove the file locally created in the project folder
            fileNameOpt = Optional.of(amazonS3.getResourceUrl(s3BucketName, fileName));
        } catch (final AmazonServiceException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(fileNameOpt);
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }
}
