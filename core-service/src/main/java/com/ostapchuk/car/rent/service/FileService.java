package com.ostapchuk.car.rent.service;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The abstraction over a file storage
 */
public interface FileService {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileService.class);

    /**
     * Uploads a file to the storage
     *
     * @param multipartFile target file to upload
     * @return the file's url
     */
    CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile);

    /**
     * Deletes images from the storage that were updated last time more than month ago
     *
     * @return amount of deleted images
     */
    CompletableFuture<Long> deleteImagesOlderThanMonth();

    /**
     * Converts {@link MultipartFile} to {@link File}
     *
     * @param multipartFile source file
     * @return converted file
     */
    default File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        Assert.notNull(multipartFile.getOriginalFilename(), "Source file name must not be null");
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException e) {
            log.error("Could not convert file: {}", e.getMessage(), e);
        }
        return file;
    }
}
