# API Service — Adaptive Image Processing Pipeline with AIOps

## Overview

The API Service is the ingestion layer of the Adaptive Image Processing Pipeline with AIOps project.

Its primary responsibility is to:

- accept image uploads from clients
- create processing jobs
- persist job metadata
- enqueue jobs for asynchronous processing
- expose APIs for job status retrieval

The service is intentionally designed to remain lightweight and stateless so that it can scale independently from processing workers.

---

# Current Architecture Position

```plaintext
Client
   ↓
API Service
   ↓
Redis
   ├── Job Metadata
   └── Job Queue
```

At the current stage of development:

- the API service is fully functional
- Redis integration is complete
- asynchronous job enqueueing is implemented
- worker-side consumption is NOT implemented yet

---

# Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| Build Tool | Maven |
| Data Store | Redis |
| API Type | REST |

---

# Implemented Features

## 1. Health Endpoint

Operational health endpoint for infrastructure validation.

### Endpoint

```http
GET /health
```

### Purpose

Used for:

- Kubernetes liveness probes
- readiness checks
- CI/CD deployment validation
- operational monitoring

### Example Response

```json
{
  "status": "UP",
  "service": "api-service"
}
```

---

## 2. Image Upload Endpoint

Allows users to upload images into the system.

### Endpoint

```http
POST /upload
```

### Request

Multipart file upload.

Example using curl:

```bash
curl -X POST \
  -F "file=@/absolute/path/to/image.png" \
  http://localhost:8080/upload
```

---

### Current Upload Flow

When a file is uploaded:

1. image is stored locally in uploads directory
2. unique job ID is generated
3. job metadata is created
4. job metadata is stored in Redis
5. job ID is pushed into Redis queue
6. response is returned immediately

---

### Example Response

```json
{
  "success": true,
  "data": {
    "jobId": "1234-5678",
    "filename": "image.png",
    "status": "PENDING"
  }
}
```

---

## 3. Job Status Endpoint

Allows clients to retrieve current job information.

### Endpoint

```http
GET /job/{jobId}
```

---

### Example Response

```json
{
  "success": true,
  "data": {
    "jobId": "1234-5678",
    "filename": "image.png",
    "status": "PENDING"
  }
}
```

---

### Example Error Response

```json
{
  "success": false,
  "message": "Job not found"
}
```

---

# Redis Integration

Redis currently serves two roles:

## 1. Job Metadata Store

Job metadata is stored using:

```plaintext
job:<jobId>
```

Example:

```plaintext
job:1234-5678
```

Stored value:

```json
{
  "jobId": "1234-5678",
  "filename": "image.png",
  "status": "PENDING"
}
```

---

## 2. Job Queue

Uploaded jobs are pushed into:

```plaintext
job_queue
```

This queue will later be consumed asynchronously by worker services.

---

# Current API Workflow

```plaintext
Client Upload
   ↓
API Service
   ↓
Store Image Locally
   ↓
Create Job Metadata
   ↓
Save Metadata to Redis
   ↓
Push Job ID to Redis Queue
   ↓
Return Response Immediately
```

---

# Current Project Structure

```plaintext
api-service/
│
├── src/
│   ├── main/
│   │   ├── java/com/paneer/api_service/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.yml
│
├── uploads/
├── pom.xml
└── README.md
```

---

# Important Design Decisions

## Stateless API Service

The API service is designed to remain stateless.

It does NOT:

- maintain in-memory session state
- process images directly
- own persistent processing state

Persistent state is externalized to:

- Redis
- shared storage

This allows:

- horizontal scaling
- multiple API instances
- independent deployment

---

## Asynchronous Architecture

The API service does not process images synchronously.

Instead:

- uploads are accepted quickly
- jobs are queued
- processing is delegated to worker services

Benefits:

- low API latency
- improved scalability
- decoupled processing

---

## Redis Queue Design

Jobs are pushed into Redis queue using:

```plaintext
LPUSH job_queue
```

Worker services will later consume jobs asynchronously.

---

# Current Limitations

At the current stage:

- image processing is NOT implemented
- worker service is NOT connected
- queue consumption is NOT implemented
- no retry logic exists yet
- no AIOps scheduling exists yet
- uploads are stored locally only
- no Docker/Kubernetes deployment yet

These will be implemented in later phases.

---

# Planned Next Steps

## Worker Service

A separate worker microservice will be implemented to:

- consume jobs from Redis
- process images asynchronously
- update job statuses

---

## Reliable Queue Pattern

The system will transition from simple queue operations to:

```plaintext
BRPOPLPUSH
```

to avoid job loss during worker failures.

---

## Image Processing

Worker service will support:

- image resizing
- compression
- format conversion

---

## AIOps Layer

A runtime AIOps engine will later:

- monitor queue length
- monitor processing latency
- detect overload conditions
- dynamically adjust scheduling strategy

---

## AI-Assisted Decision Layer

An optional AI agent will assist with:

- NORMAL vs OVERLOAD mode selection

based on runtime metrics.

---

## Containerization

The API service will later be containerized using Docker.

---

## Kubernetes Deployment

Future deployment targets include:

- API pods
- worker pods
- Redis pod
- shared persistent volume

---

## Monitoring and Logging

Future monitoring stack:

- Elasticsearch
- Logstash
- Kibana

Operational logs and AIOps decisions will later be visualized through Kibana dashboards.

---

# Running the Service

## Start Redis

```bash
docker run -d \
  --name redis-dev \
  -p 6379:6379 \
  redis
```

---

## Start Spring Boot Application

```bash
./mvnw spring-boot:run
```

---

## Default Port

```plaintext
8080
```

---

# Configuration

Current Redis configuration:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

---

# Summary

The API Service currently provides:

- REST-based image upload
- job metadata creation
- Redis-backed persistence
- asynchronous job enqueueing
- consistent API responses
- operational health endpoint

This forms the ingestion and orchestration entry point of the larger distributed image processing pipeline.

