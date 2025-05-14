package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.PostCreateRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.PostResponse;
import com.example.courseapplicationproject.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PostController {
    PostService postService;

    // API tạo bài viết
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> createPost(@ModelAttribute PostCreateRequest request) {
        PostResponse postResponse = postService.createPost(request);
        return ApiResponse.success(postResponse, "Tạo bài viết thành công");
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(
            @PathVariable("id") Long postId,
            @ModelAttribute PostCreateRequest request) {

        PostResponse updatedPost = postService.updatePost(postId, request);
        return ApiResponse.success(updatedPost, "Cập nhật bài viết thành công");
    }
    @GetMapping("/all")
    public ApiResponse<Page<PostResponse>> getAllPosts(
            @RequestParam("keyword") String keyword,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        return ApiResponse.success(postService.getPosts(keyword, page, size),"OK");
    }
    @GetMapping("/all/sort-view")
    public ApiResponse<Page<PostResponse>> getAllPostsSortViewDESC(
            @RequestParam("keyword") String keyword,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        return ApiResponse.success(postService.getPostsSortView(keyword, page, size),"OK");
    }

    // API lấy bài viết theo slug
    @GetMapping("/{slug}")
    public ApiResponse<PostResponse> getPostBySlug(@PathVariable String slug) {
        PostResponse postResponse = postService.getPostBySlug(slug);
        return ApiResponse.success(postResponse, "Lấy bài viết thành công");
    }

    // API toggle trạng thái published của bài viết
    @PutMapping("/{postId}/toggle-publish")
    public ApiResponse<PostResponse> togglePublished(@PathVariable Long postId) {
        PostResponse postResponse = postService.togglePublished(postId);
        return ApiResponse.success(postResponse, "Toggle trạng thái thành công");
    }

    // API xóa bài viết
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ApiResponse.success(null, "Xóa bài viết thành công");
    }
}
