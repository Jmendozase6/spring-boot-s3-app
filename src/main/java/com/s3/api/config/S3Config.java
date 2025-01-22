package com.s3.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.service.region}")
    private String serviceRegion;

    /**
     * S3 Client Synchronous
     */
    @Bean
    public S3Client getS3Client() {
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(serviceRegion))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

    /**
     * S3 Client Asynchronous
     */
    @Bean
    public S3AsyncClient getS3AsyncClient() {
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(serviceRegion))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

    /**
     * Obtain a pre-signed URL for an operation
     */
    @Bean
    public S3Presigner getS3Presigner() {
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

}
