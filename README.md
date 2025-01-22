# Spring Boot S3 App

This is a Spring Boot application that interacts with AWS S3 for various operations such as creating buckets, uploading files, downloading files, and generating pre-signed URLs.

## Technologies Used

- Java
- Spring Boot
- Maven
- AWS S3

## Prerequisites

- Java 11 or higher
- Maven
- AWS account with S3 access

## Configuration

Update the `src/main/resources/application.properties` file with your AWS credentials and S3 configuration:

```ini
spring.application.name=spring-boot-s3-app

# AWS S3
aws.access.key=YOUR_AWS_ACCESS_KEY
aws.secret.key=YOUR_AWS_SECRET_KEY
aws.region=us-east-1
aws.serviceRegion=https://s3.us-east-1.amazonaws.com

# Files Config
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.destination.folder=src/main/resources/static/

# Logging
logging.level.software.amazon.awssdk=DEBUG