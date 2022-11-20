package com.ostapchuk.car.rent.scheduler;

import com.ostapchuk.car.rent.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler consists of tasks to be done repeatedly
 */
@Component
@RequiredArgsConstructor
public class ApplicationScheduler {

    private final FileService fileService;

    /**
     * Deletes images on the first day of a month
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void deleteImagesEachMonth() {
        fileService.deleteImagesOlderThanMonth();
    }
}
