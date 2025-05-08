package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.QuizByIdResponse;
import com.example.courseapplicationproject.service.QuizService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuizController {

    QuizService quizService;

    // ===== QUIZ =====
    @GetMapping("/{quizId}")
    public ApiResponse<QuizByIdResponse> getQuizById(@PathVariable Integer quizId) {
        return ApiResponse.success(quizService.getQuizById(quizId),"OK");
    }
    @GetMapping("/instructor/course/{courseId}")
    public ApiResponse<List<QuizByIdResponse>> getQuizzesInstructor(
            @PathVariable Long courseId
    ) {
        return ApiResponse.success(quizService.getQuizzesForInstructor(courseId),"OK");
    }

    @PostMapping("/create")
    public ApiResponse<Void> createQuiz(@RequestBody QuizRequest request) {
        quizService.createQuiz(request);
        return ApiResponse.success(null, "Quiz created successfully");
    }

    @PutMapping("/update")
    public ApiResponse<Void> updateQuiz(@RequestBody QuizRequest request) {
        quizService.updateQuiz(request);
        return ApiResponse.success(null, "Quiz updated successfully");
    }

    @DeleteMapping("/delete/{quizId}")
    public ApiResponse<Void> deleteQuiz(@PathVariable Integer quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.success(null, "Quiz deleted successfully");
    }

    // ===== QUIZ QUESTION =====

    @PostMapping("/question/create")
    public ApiResponse<Void> createQuizQuestion(@RequestBody QuizQuestionRequest request) {
        quizService.createQuizQuestion(request);
        return ApiResponse.success(null, "Question created successfully");
    }

    @PutMapping("/question/update")
    public ApiResponse<Void> updateQuizQuestion(@RequestBody QuizQuestionRequest request) {
        quizService.updateQuizQuestion(request);
        return ApiResponse.success(null, "Question updated successfully");
    }

    @DeleteMapping("/question/delete/{questionId}")
    public ApiResponse<Void> deleteQuizQuestion(@PathVariable Integer questionId) {
        quizService.deleteQuizQuestion(questionId);
        return ApiResponse.success(null, "Question deleted successfully");
    }

    // ===== QUESTION ANSWER =====

    @PostMapping("/answer/create")
    public ApiResponse<Void> createAnswer(@RequestBody QuestionAnswerRequest request) {
        quizService.createAnswer(request);
        return ApiResponse.success(null, "Answer created successfully");
    }

    @PutMapping("/answer/update")
    public ApiResponse<Void> updateAnswer(@RequestBody QuestionAnswerRequest request) {
        quizService.updateAnswer(request);
        return ApiResponse.success(null, "Answer updated successfully");
    }

    @DeleteMapping("/answer/delete/{answerId}")
    public ApiResponse<Void> deleteAnswer(@PathVariable Integer answerId) {
        quizService.deleteAnswer(answerId);
        return ApiResponse.success(null, "Answer deleted successfully");
    }
    @PostMapping("/create-full")
    public ApiResponse<Void> createFullQuiz(@RequestBody CreateFullQuizRequest request) {
        quizService.createFullQuiz(request);
        return ApiResponse.success(null, "Quiz created successfully");
    }
    @PutMapping("/update-full")
    public ApiResponse<Void> updateFullQuiz(@RequestBody UpdateFullQuizRequest request) {
        quizService.updateFullQuiz(request);
        return ApiResponse.success(null, "Quiz updated successfully");
    }
}
