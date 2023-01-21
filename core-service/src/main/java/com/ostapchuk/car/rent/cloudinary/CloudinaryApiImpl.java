package com.ostapchuk.car.rent.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Search;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
class CloudinaryApiImpl implements CloudinaryApi {

    private final ObjectMapper mapper;
    private final Cloudinary cloudinary;

    @Async
    @Override
    public CompletableFuture<Optional<SearchApiResponse>> searchImagesOlderThanOneMonth(
            final Optional<String> nextCursor) {
        final int limit = 100;
        final String imagesThatUploadedMoreThanOneMonthAgo = "resource_type:image AND uploaded_at>1m";
        final Search search = cloudinary.search()
                .expression(imagesThatUploadedMoreThanOneMonthAgo)
                .maxResults(limit);
        nextCursor.ifPresent(search::nextCursor);
        try {
            return CompletableFuture.completedFuture(
                    Optional.ofNullable(search.execute()).map(r -> mapper.convertValue(r, SearchApiResponse.class))
            );
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Override
    public CompletableFuture<Integer> deleteResources(final List<CloudinaryResource> resources) {
        final List<String> publicIds = resources.stream().map(CloudinaryResource::publicId).toList();
        try {
            cloudinary.api().deleteResources(publicIds, Collections.emptyMap());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(publicIds.size());
    }

    @Async
    @Override
    public CompletableFuture<UploadApiResponse> uploadFile(final File file) {
        final Map<String, String> params =
                mapper.convertValue(new UploadRequest(UUID.randomUUID().toString()), new TypeReference<>() {
                });
        try {
            return CompletableFuture.completedFuture(
                    mapper.convertValue(cloudinary.uploader().upload(file, params), UploadApiResponse.class));
        } catch (final IOException e) {
            log.error("Could not upload file {}: {}", file.getName(), e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

}
