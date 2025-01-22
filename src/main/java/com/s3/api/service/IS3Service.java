package com.s3.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface IS3Service {

    /**
     * Create a bucket
     *
     * @param bucketName bucket name
     * @return bucket name
     */
    String createBucket(String bucketName);

    /**
     * Check if bucket exist
     *
     * @param bucketName bucket name
     * @return bucket name if exist else null
     */
    String doesBucketExist (String bucketName);

    /**
     * List all buckets
     *
     * @return list of buckets
     */
    List<String> getAllBuckets();

    /**
     * Upload file to S3
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param filePath   path of the file
     * @return true if file uploaded successfully else false
     */
    Boolean uploadFile(String bucketName, String key, Path filePath);

    /**
     * Download file from S3
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @throws IOException if file not found
     */
    void downloadFile(String bucketName, String key) throws IOException;

    /**
     * Generate a pre-signed URL for download
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param duration   duration of the URL
     * @return pre-signed URL
     */
    String generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    /**
     * Generate a pre-signed URL for upload
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param duration   duration of the URL
     * @return pre-signed URL
     */
    String generatePresignedDownloadUrl(String bucketName, String key, Duration duration);

}
