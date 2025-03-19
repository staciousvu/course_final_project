package com.example.courseapplicationproject.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    UserRepository userRepository;
    static String folderImage = "/uploads/image";
    static String folderVideo = "/uploads/video";
    Cloudinary cloudinary;

    public CloudinaryService(UserRepository userRepository, @Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.userRepository = userRepository;
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public Map uploadImage(MultipartFile file) {
        Map uploadParams = ObjectUtils.asMap(
                "resource_type", "image", "folder", folderImage, "use_filename", false, "overwrite", false);
        Map uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return uploadResult;
    }

    public void updateImage(String url) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setAvatar(url);
        userRepository.save(user);
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Map> uploadVideoAuto(MultipartFile file) {
        long fileSizeInMB = file.getSize() / (1024 * 1024); // Chuyển byte -> MB

        if (fileSizeInMB > 100) {
            // File lớn hơn 100MB -> Dùng chunked upload
            return CompletableFuture.completedFuture(uploadLargeVideo(file));
        } else {
            // File nhỏ hơn 100MB -> Dùng async upload
            return uploadVideoAsync(file);
        }
    }
    // Dùng async upload
    @Async
    public CompletableFuture<Map> uploadVideoAsync(MultipartFile file) {
        Map uploadParams = ObjectUtils.asMap(
                "resource_type",
                "video",
                "folder",
                folderVideo,
                "use_filename",
                false,
                "unique_filename",
                true,
                "overwrite",
                false,
                "quality",
                "auto:low");
        Map uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(uploadResult);
    }
    // Dùng chunked upload
    // Upload video lớn bằng phương pháp chunked upload
    // Chia nhỏ file thành từng phần (mỗi chunk 6MB) để upload nhanh hơn
    public Map uploadLargeVideo(MultipartFile file) {
        Map uploadParams = ObjectUtils.asMap(
                "resource_type", "video", "chunk_size", 6000000 // Chia file thành từng chunk 6MB
                );
        try {
            return cloudinary.uploader().uploadLarge(file.getBytes(), uploadParams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteVideo(String publicId) {
        Map result = null;
        try {
            result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.get("result").toString();
    }
}
