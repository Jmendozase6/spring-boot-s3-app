package com.s3.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class S3ServiceImpl implements IS3Service {

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    /**
     * Create a bucket
     *
     * @param bucketName bucket name
     * @return bucket name
     */
    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse response = s3Client.createBucket(bucketBuilder -> bucketBuilder.bucket(bucketName));
        return "Bucket created: " + response.location();
    }

    /**
     * Check if bucket exist
     *
     * @param bucketName bucket name
     * @return bucket name if exist else null
     */
    @Override
    public String doesBucketExist(String bucketName) {
        try {
            s3Client.headBucket(headBucket -> headBucket.bucket(bucketName));
            return "Bucket ".concat(bucketName).concat(" exist");
        } catch (S3Exception exception) {
            return "Bucket ".concat(bucketName).concat(" does not exist");
        }
    }

    /**
     * List all buckets
     *
     * @return list of buckets
     */
    @Override
    public List<String> getAllBuckets() {
        ListBucketsResponse bucketsResponse = s3Client.listBuckets();
        if (bucketsResponse.hasBuckets()) {
            return bucketsResponse.buckets()
                    .stream()
                    .map(
                            Bucket::name
                    ).toList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Upload file to S3
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param filePath   path of the file
     * @return true if file uploaded successfully else false
     */
    @Override
    public Boolean uploadFile(String bucketName, String key, Path filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, filePath);
        return putObjectResponse.sdkHttpResponse().isSuccessful();
    }

    /**
     * Download file from S3
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @throws IOException if file not found
     */
    @Override
    public void downloadFile(String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(getObjectRequest);

        String fileName;
        if (key.contains("/")) {
            fileName = key.substring(key.lastIndexOf("/"));
        } else {
            fileName = key;
        }

        String filePath = Paths.get(destinationFolder, fileName).toString();
        File file = new File(filePath);
        file.getParentFile().mkdir();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(objectAsBytes.asByteArray());
        } catch (IOException exception) {
            throw new IOException("Error to download file: " + exception.getCause());
        }

    }

    /**
     * Generate a pre-signed URL for download
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param duration   duration of the URL
     * @return pre-signed URL
     */
    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        URL presignedUrl = presignedPutObjectRequest.url();
        return presignedUrl.toString();
    }

    /**
     * Generate a pre-signed URL for upload
     *
     * @param bucketName bucket name
     * @param key        name of the file
     * @param duration   duration of the URL
     * @return pre-signed URL
     */
    @Override
    public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest objectRequest = s3Presigner.presignGetObject(presignRequest);
        URL presignedUrl = objectRequest.url();
        return presignedUrl.toString();
    }
}
