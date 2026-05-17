package com.paneer.api_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paneer.api_service.model.Job;
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

    public void enqueueJob(String jobId) {

        redisTemplate.opsForList()
                .leftPush("job_queue", jobId);
    }

    public void saveJob(Job job)
            throws JsonProcessingException {

        String key = "job:" + job.getJobId();

        String value =
                objectMapper.writeValueAsString(job);

        redisTemplate.opsForValue().set(key, value, Duration.ofHours(1));
    }

    public Job getJob(String jobId)
            throws JsonProcessingException {

        String key = "job:" + jobId;

        String value =
                redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return objectMapper.readValue(value, Job.class);
    }
}