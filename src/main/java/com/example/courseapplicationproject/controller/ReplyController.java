package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.ReplyRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.ReplyResponse;
import com.example.courseapplicationproject.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reply")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReplyController {

    ReplyService replyService;

    @GetMapping
    public ApiResponse<List<ReplyResponse>> getRepliesByDiscussion(@RequestParam Long discussionId) {
        List<ReplyResponse> replies = replyService.getRepliesByDiscussion(discussionId);
        return ApiResponse.success(replies, "Lấy danh sách phản hồi thành công");
    }

    @PostMapping
    public ApiResponse<ReplyResponse> createReply(@RequestParam Long discussionId,
                                                  @RequestBody ReplyRequest replyRequest) {
        ReplyResponse replyResponse = replyService.createReply(discussionId, replyRequest);
        return ApiResponse.success(replyResponse, "Tạo phản hồi thành công");
    }
}
