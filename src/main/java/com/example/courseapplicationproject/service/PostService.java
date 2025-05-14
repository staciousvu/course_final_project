package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.PostCreateRequest;
import com.example.courseapplicationproject.dto.response.PostResponse;
import com.example.courseapplicationproject.entity.Post;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.PostRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class PostService {
    PostRepository postRepository;
    CloudinaryService cloudinaryService;
    UserRepository userRepository;
    public PostResponse createPost(PostCreateRequest request) {
        // 1. Tìm tác giả
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Upload ảnh nếu có

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            Map result = cloudinaryService.uploadImage(request.getImage());
            imageUrl = result.get("secure_url").toString();
        }
        String slug = generateSlug(request.getTitle());

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrl(imageUrl);
        post.setIsPublished(request.getIsPublished());
        post.setAuthor(author);
        post.setView(0);
        post.setSlug(slug);

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost);
    }
    public PostResponse updatePost(Long postId, PostCreateRequest request) {
        // 1. Lấy email từ context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm bài viết
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 3. Kiểm tra quyền (nếu bạn muốn chỉ tác giả mới được sửa)
//        if (!post.getAuthor().getId().equals(author.getId())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }

        // 4. Cập nhật thông tin
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublished(request.getIsPublished());

        // 5. Nếu có ảnh mới thì upload và cập nhật
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            Map result = cloudinaryService.uploadImage(request.getImage());
            String imageUrl = result.get("secure_url").toString();
            post.setImageUrl(imageUrl);
        }

        // 6. Cập nhật lại slug nếu tiêu đề thay đổi
        String newSlug = generateSlug(request.getTitle());
        post.setSlug(newSlug);

        // 7. Lưu và trả về kết quả
        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost);
    }

    public Page<PostResponse> getPosts(String keyword,Integer page,Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Post> postPage = postRepository.findAll(keyword, pageRequest);
        return postPage.map(this::mapToPostResponse);
    }
    public Page<PostResponse> getPostsSortView(String keyword,Integer page,Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "view");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Post> postPage = postRepository.findAll(keyword, pageRequest);
        return postPage.map(this::mapToPostResponse);
    }
    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        post.setView(post.getView() + 1);
        postRepository.save(post);
        return mapToPostResponse(post);
    }

    public PostResponse togglePublished(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        post.setIsPublished(!post.getIsPublished());
        Post updatedPost = postRepository.save(post);

        return mapToPostResponse(updatedPost);
    }
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    private String generateSlug(String title) {
        String baseSlug = title.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
        String uuid = UUID.randomUUID().toString().substring(0, 8); // Lấy 8 ký tự đầu cho gọn
        return baseSlug + "-" + uuid;
    }
    private PostResponse mapToPostResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setIsPublished(post.getIsPublished());
        response.setSlug(post.getSlug());
        response.setAuthorFullname(post.getAuthor().getFirstName()+" "+post.getAuthor().getLastName());
        response.setAuthorEmail(post.getAuthor().getEmail());
        response.setAuthorAvatar(post.getAuthor().getAvatar());
        return response;
    }
}
