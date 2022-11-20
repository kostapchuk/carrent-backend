package com.ostapchuk.car.rent.cloudinary;

import com.cloudinary.Cloudinary;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the client over {@link Cloudinary} to simplify usage of the API
 */
interface CloudinaryApi {
    /**
     * Searches images that were uploaded last time more than month ago. Uses nextCursor to search by it if it's
     * present. Max amount of found images is 100
     *
     * @param nextCursor cursor to search by if present
     * @return cloudinary search api response
     */
    CompletableFuture<Optional<SearchApiResponse>> searchImagesOlderThanOneMonth(Optional<String> nextCursor);

    /**
     * Deletes images by public id
     *
     * @param resources images
     * @return amount of deleted images
     */
    CompletableFuture<Integer> deleteResources(List<CloudinaryResource> resources);

    /**
     * Uploads the file to storage
     *
     * @param file to be uploaded
     * @return wrapped response of the Cloudinary API
     */
    CompletableFuture<UploadApiResponse> uploadFile(File file);
}
