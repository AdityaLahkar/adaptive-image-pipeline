package com.paneer.worker_service.service;

import com.paneer.worker_service.model.Job;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisConsumerService {

    private final StringRedisTemplate redisTemplate;
    private final RedisJobService redisJobService;
    private final ImageProcessingService imageProcessingService;

    public RedisConsumerService(StringRedisTemplate redisTemplate, RedisJobService redisJobService, ImageProcessingService imageProcessingService) {
        this.redisTemplate = redisTemplate;
        this.redisJobService = redisJobService;
        this.imageProcessingService = imageProcessingService;
    }

    @PostConstruct
    public void startConsumer() {

        Thread consumerThread = new Thread(() -> {

            while (true) {

                try {

                    String jobId = redisTemplate.opsForList().rightPopAndLeftPush("job_queue", "processing_queue");

                    if (jobId != null) {

                        Job job = redisJobService.getJob(jobId);

                        if (job != null) {
                            job.setStatus("PROCESSING");
                            redisJobService.saveJob(job);
                            String outputFileName;

                            switch (job.getOperation()) {

                                case "resize":

                                    outputFileName = imageProcessingService.resize(job.getFilename(), job.getWidth(), job.getHeight());
                                    job.setOutputFilename(outputFileName);
                                    break;

                                case "compress":

                                    outputFileName = imageProcessingService.compress(job.getFilename(), job.getQuality());
                                    job.setOutputFilename(outputFileName);
                                    break;

                                case "convert":

                                    outputFileName = imageProcessingService.convertToJpg(job.getFilename());
                                    job.setOutputFilename(outputFileName);
                                    break;

                                default:

                                    job.setStatus("FAILED");
                                    redisJobService.saveJob(job);
                                    System.out.println("Unknown operation: " + job.getOperation());
                                    continue;
                            }

                            job.setStatus("COMPLETED");
                            redisJobService.saveJob(job);

                        }
                        redisTemplate.opsForList().remove("processing_queue", 1, jobId);
                    }

                    Thread.sleep(1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        consumerThread.start();
    }
}