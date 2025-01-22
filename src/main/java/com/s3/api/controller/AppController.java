package com.s3.api.controller;

import com.s3.api.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/s3")
public class AppController {

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private IS3Service service;

    /**
     * @see IS3Service#createBucket(String)
     */
    @PostMapping("/createBucket")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName) {
        return ResponseEntity.ok(service.createBucket(bucketName));
    }

    /**
     * @see IS3Service#doesBucketExist(String)
     */
    @GetMapping("/doesBucketExist/{bucketName}")
    public ResponseEntity<String> checkBucket(@PathVariable String bucketName) {
        return ResponseEntity.ok(service.doesBucketExist(bucketName));
    }

    /**
     * @see IS3Service#getAllBuckets()
     */
    @GetMapping("/getAllBuckets")
    public ResponseEntity<List<String>> getAllBuckets() {
        return ResponseEntity.ok(service.getAllBuckets());
    }

    /**
     * @see IS3Service#uploadFile(String, String, Path)
     */
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam String bucketName,
                                             @RequestParam String key,
                                             @RequestPart MultipartFile file) throws IOException {
        try {
            Path staticDir = Paths.get(destinationFolder);
            if (!Files.exists(staticDir)) {
                Files.createDirectories(staticDir);
            }
            Path filePath = staticDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Path finalPath = Files.write(filePath, file.getBytes());
            Boolean uploadResponse = service.uploadFile(bucketName, key, finalPath);
            if (uploadResponse) {
                Files.delete(finalPath);
                return ResponseEntity.ok("File uploaded successfully");
            } else {
                return ResponseEntity.internalServerError().body("Error uploading file");
            }
        } catch (IOException e) {
            throw new IOException("Error" + e.getMessage());
        }
    }

    /**
     * @see IS3Service#downloadFile(String, String)
     */
    @PostMapping("/downloadFile")
    public ResponseEntity<String> downloadFile(@RequestParam String bucketName,
                                               @RequestParam String key) throws IOException {
        service.downloadFile(bucketName, key);
        return ResponseEntity.ok("File download successfully");
    }

    /**
     * @see IS3Service#generatePresignedUploadUrl(String, String, Duration)
     */
    @PostMapping("/generatePresignedUploadUrl")
    public ResponseEntity<String> generatePresignedUploadUrl(@RequestParam String bucketName,
                                                             @RequestParam String key,
                                                             @RequestParam Long minutes) {
        Duration durationToLive = Duration.ofMinutes(minutes);
        return ResponseEntity.ok(service.generatePresignedUploadUrl(bucketName, key, durationToLive));
    }

    /**
     * @see IS3Service#generatePresignedDownloadUrl(String, String, Duration)
     */
    @PostMapping("/generatePresignedDownloadUrl")
    public ResponseEntity<String> generatePresignedDownloadUrl(@RequestParam String bucketName,
                                                               @RequestParam String key,
                                                               @RequestParam Long minutes) {
        Duration durationToLive = Duration.ofMinutes(minutes);
        return ResponseEntity.ok(service.generatePresignedDownloadUrl(bucketName, key, durationToLive));
    }

}
