package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.entity.Slide;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.SlideRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class SlideService {
    SlideRepository slideRepository;
    CloudinaryService cloudinaryService;


    public Slide createSlide(MultipartFile imageFile) {
        Map result = cloudinaryService.uploadImage(imageFile);
        String imageUrl = result.get("secure_url").toString();
        Slide slide = new Slide();
        slide.setUrlImage(imageUrl);
        slide.setPosition(slideRepository.count() + 1);
        slide.setIsActive(true);
        return slideRepository.save(slide);
    }
    public Slide createSlideUrlNetwork(String urlImage) {
        Slide slide = new Slide();
        slide.setUrlImage(urlImage);
        slide.setPosition(slideRepository.count() + 1);
        slide.setIsActive(true);
        return slideRepository.save(slide);
    }
    public Slide updateSlidePosition(Long id, Long newPosition) {
        Slide slide = slideRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SLIDE_NOT_FOUND));
        slide.setPosition(newPosition);
        return slideRepository.save(slide);
    }


    public List<Slide> getAllSlidesSortedByPosition() {
        return slideRepository.findAllByOrderByPositionAsc();
    }

    public Slide toggleSlideActive(Long id) {
        Slide slide = slideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slide not found"));
        slide.setIsActive(!slide.getIsActive());
        return slideRepository.save(slide);
    }

    public void deleteSlide(Long id) {
        slideRepository.deleteById(id);
    }
}
