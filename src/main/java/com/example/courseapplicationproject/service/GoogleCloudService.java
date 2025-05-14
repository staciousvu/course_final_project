package com.example.courseapplicationproject.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class GoogleCloudService {
    Storage storage;
    String bucketName = "course_final_project";

    public GoogleCloudService() throws IOException {
        // Tạo đối tượng Storage kết nối tới Google Cloud Storage
        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new FileInputStream("src/main/resources/gcs/gcs.json")
                ))
                .build()
                .getService();
    }

    // Phương thức upload file lên Google Cloud Storage
    public String uploadFile(String fileName, byte[] content, String contentType) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();
        storage.create(blobInfo, content);
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}
