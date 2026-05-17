package com.paneer.worker_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paneer.worker_service.model.Job;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisJobService {

    private final StringRedisTemplate redisTemplate;


    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public RedisJobService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public Job getJob(String jobId)
            throws JsonProcessingException {

        String value = redisTemplate
                .opsForValue()
                .get("job:" + jobId);

        if (value == null) {
            return null;
        }

        return objectMapper.readValue(
                value,
                Job.class
        );
    }

    public void saveJob(Job job)
            throws JsonProcessingException {

        String value =
                objectMapper.writeValueAsString(job);

        redisTemplate
                .opsForValue()
                .set(
                        "job:" + job.getJobId(),
                        value,
                        Duration.ofHours(1)
                );
    }
}