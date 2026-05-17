package com.paneer.api_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paneer.api_service.service.RedisJobService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.paneer.api_service.model.Job;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
public class UploadController {

    private static final String UPLOAD_DIR = "/uploads/";
    private final RedisJobService redisJobService;

    public UploadController(RedisJobService redisJobService) {
        this.redisJobService = redisJobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,

                                         @RequestParam("operation") String operation,

                                         @RequestParam(required = false) Integer width,

                                         @RequestParam(required = false) Integer height,

                                         @RequestParam(required = false) Float quality,

                                         @RequestParam(required = false) String targetFormat) {

        try {

            if (file.isEmpty()) {

                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "File is empty"));
            }

            if (!operation.equals("resize") && !operation.equals("compress") && !operation.equals("convert")) {

                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid operation"));
            }

            if (operation.equals("resize")) {

                if (width == null || height == null) {

                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Width and height required"));
                }
                if (width <= 0 || height <= 0) {

                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Width and height must be positive"));
                }
            }

            if (operation.equals("compress")) {

                if (quality == null) {

                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Quality required"));
                }

                if (quality < 0.1f || quality > 1.0f) {

                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Quality must be between 0.1 and 1.0"));
                }
            }

            if (operation.equals("convert")) {

                if (targetFormat == null || !targetFormat.equals("jpg")) {

                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Only jpg conversion supported"));
                }
            }

            File dir = new File(UPLOAD_DIR);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String jobId = UUID.randomUUID().toString();

            String filename = jobId + "_" + file.getOriginalFilename();

            File destination = new File(UPLOAD_DIR + filename);

            file.transferTo(destination);
            Job job = new Job(jobId, filename, "PENDING", operation, width, height, quality, targetFormat, null);
            redisJobService.saveJob(job);

            redisJobService.enqueueJob(jobId);
            while (true) {

                Job updatedJob = redisJobService.getJob(jobId);

                if (updatedJob != null) {

                    if (updatedJob.getStatus().equals("COMPLETED")) {

                        File processedFile = new File("/processed/" + updatedJob.getOutputFilename());

                        Resource resource = new FileSystemResource(processedFile);

                        new Thread(() -> {

                            try {

                                Thread.sleep(10000);

                                destination.delete();

                                processedFile.delete();

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        }).start();

                        String contentType = java.nio.file.Files.probeContentType(processedFile.toPath());

                        if (contentType == null) {
                            contentType = "application/octet-stream";
                        }

                        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + processedFile.getName() + "\"").header(HttpHeaders.CONTENT_TYPE, contentType).contentLength(processedFile.length()).body(resource);
                    }

                    if (updatedJob.getStatus().equals("FAILED")) {

                        return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Processing failed"));
                    }
                }

                Thread.sleep(500);
            }

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Upload failed"));
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJob(@PathVariable String jobId) {

        Job job;

        try {
            job = redisJobService.getJob(jobId);

        } catch (JsonProcessingException e) {

            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Failed to fetch job"));
        }

        if (job == null) {

            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Job not found"));
        }

        return ResponseEntity.ok(Map.of("success", true, "data", job));
    }
}