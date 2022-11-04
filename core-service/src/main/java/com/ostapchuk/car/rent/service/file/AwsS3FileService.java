package com.ostapchuk.car.rent.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class AwsS3FileService implements FileService {
//    private final AmazonS3Client client;
//
//    @Value("${aws.s3.bucket.name}")
//    private final String s3BucketName;
//
    @Async
    public CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile) {
        throw new UnsupportedOperationException("Not implemented");
//        Optional<String> fileNameOpt = Optional.empty();
//        try {
//            final File file = convertMultiPartFileToFile(multipartFile);
//            final String fileName = LocalDateTime.now() + Constant.UNDERSCORE + UUID.randomUUID();
//            final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, file);
//            client.putObject(putObjectRequest);
//            log.info("Uploading file with name {}", fileName);
//            Files.delete(file.toPath()); // Remove the file locally created in the project folder
//            fileNameOpt = Optional.of(client.getResourceUrl(s3BucketName, fileName));
//        } catch (final AmazonServiceException | IOException e) {
//            log.error(e.getMessage(), e);
//        }
//        return CompletableFuture.completedFuture(fileNameOpt);
    }
//
//    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
//        final File file = new File(multipartFile.getOriginalFilename());
//        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
//            outputStream.write(multipartFile.getBytes());
//        } catch (final IOException e) {
//            log.error(e.getMessage(), e);
//        }
//        return file;
//    }
}
