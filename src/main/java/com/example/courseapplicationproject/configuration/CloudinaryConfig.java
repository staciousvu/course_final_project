// package com.example.courseapplicationproject.configuration;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import com.cloudinary.Cloudinary;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Configuration
// public class CloudinaryConfig {
//    @Value("${CLOUDINARY_URL}")
//    protected String cloudinaryUrl;
//
//    @Bean
//    public Cloudinary cloudinary() {
//        return new Cloudinary(cloudinaryUrl);
//    }
// }
