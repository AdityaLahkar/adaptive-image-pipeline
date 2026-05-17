package com.paneer.worker_service.model;

public class Job {

    private String jobId;
    private String filename;
    private String status;
    private String operation;
    private Integer width;
    private Integer height;
    private Float quality;
    private String targetFormat;
    private String outputFilename;


    public Job() {
    }

    public Job(String jobId, String filename, String status, String operation, Integer width, Integer height, Float quality, String targetFormat, String outputFilename) {
        this.jobId = jobId;
        this.filename = filename;
        this.status = status;
        this.operation = operation;
        this.width = width;
        this.height = height;
        this.quality = quality;
        this.targetFormat = targetFormat;
        this.outputFilename = outputFilename;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getQuality() {
        return quality;
    }

    public void setQuality(Float quality) {
        this.quality = quality;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }
}