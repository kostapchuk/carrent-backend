package com.ostapchuk.car.rent.schedule;

import com.ostapchuk.car.rent.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cloudinary scheduler consists of tasks to be done repeatedly
 */
@Component
@RequiredArgsConstructor
public class CloudinaryScheduler {

    private final FileService fileService;

    /**
     * Deletes images on the first day of a month
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void deleteImagesEachMonth() {
        fileService.deleteImagesOlderThanMonth();
    }
}
