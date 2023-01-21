package com.ostapchuk.car.rent.cloudinary;

import com.ostapchuk.car.rent.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
class CloudinaryFileService implements FileService {

    private final CloudinaryApi api;

    @Override
    public CompletableFuture<Optional<String>> upload(final MultipartFile multipartFile) {
        final File file = convertMultiPartFileToFile(multipartFile);
        return api.uploadFile(file).thenApply(r -> Optional.ofNullable(r.url()));
    }

    @Override
    public CompletableFuture<Long> deleteImagesOlderThanMonth() {
        final AtomicLong amount = new AtomicLong();
        Optional<String> nextCursor = Optional.empty();
        do {
            nextCursor = api.searchImagesOlderThanOneMonth(nextCursor)
                    .thenApply(r -> {
                        final List<CloudinaryResource> resources =
                                r.map(SearchApiResponse::resources).orElse(Collections.emptyList());
                        api.deleteResources(resources).thenAccept(amount::addAndGet);
                        return r;
                    })
                    .thenApply(r -> r.map(SearchApiResponse::nextCursor)).join();
        } while (nextCursor.isPresent());
        return CompletableFuture.completedFuture(amount.get());
    }
}
