package com.scity.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
public class MemoryUsageLogger {
    @Scheduled(fixedRate = 300000) // Log memory usage every 60 seconds
    public void logMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

//        log.info("Total Memory: {} MB, Free Memory: {} MB, Used Memory: {} MB",
//                totalMemory / (1024 * 1024),
//                freeMemory / (1024 * 1024),
//                usedMemory / (1024 * 1024));
        //System.gc();
    }
}