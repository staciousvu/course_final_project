package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.QuizByIdResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class QuizService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    QuizRepository quizRepository;
    QuizQuestionRepository quizQuestionRepository;
    QuestionAnswerRepository questionAnswerRepository;

    public void createQuiz(QuizRequest quizRequest){
        Course course = courseRepository.findById(quizRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Quiz quiz = Quiz.builder()
                .title(quizRequest.getTitle())
                .description(quizRequest.getDescription())
                .course(course)
                .build();
        quizRepository.save(quiz);
    }
    public void updateQuiz(QuizRequest quizRequest){
        Quiz quiz = quizRepository.findById(quizRequest.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        quiz.setTitle(quizRequest.getTitle());
        quiz.setDescription(quizRequest.getDescription());
        quizRepository.save(quiz);
    }
    public void deleteQuiz(Integer quizId){
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        quiz.setIsDeleted(true);
        quizRepository.save(quiz);
    }
    public void createQuizQuestion(QuizQuestionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

        QuizQuestion question = QuizQuestion.builder()
                .quiz(quiz)
                .content(request.getContent())
                .build();

        quizQuestionRepository.save(question);
    }

    public void updateQuizQuestion(QuizQuestionRequest request) {
        QuizQuestion question = quizQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        question.setContent(request.getContent());
        quizQuestionRepository.save(question);
    }

    public void deleteQuizQuestion(Integer questionId) {
        quizQuestionRepository.deleteById(questionId);
    }

    public void createAnswer(QuestionAnswerRequest request) {
        QuizQuestion question = quizQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionAnswer answer = QuestionAnswer.builder()
                .question(question)
                .content(request.getContent())
                .isCorrect(request.getIsCorrect())
                .build();

        questionAnswerRepository.save(answer);
    }

    public void updateAnswer(QuestionAnswerRequest request) {
        QuestionAnswer answer = questionAnswerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_FOUND));

        answer.setContent(request.getContent());
        answer.setIsCorrect(request.getIsCorrect());

        questionAnswerRepository.save(answer);
    }

    public void deleteAnswer(Integer answerId) {
        questionAnswerRepository.deleteById(answerId);
    }
    public void createFullQuiz(CreateFullQuizRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .isDeleted(false)
                .build();

        List<QuizQuestion> quizQuestions = request.getQuestions().stream()
                .map(q -> {
                    QuizQuestion question = QuizQuestion.builder()
                            .quiz(quiz)
                            .content(q.getContent())
                            .build();

                    List<QuestionAnswer> answers = q.getAnswers().stream()
                            .map(a -> QuestionAnswer.builder()
                                    .question(question)
                                    .content(a.getContent())
                                    .isCorrect(a.getIsCorrect())
                                    .build())
                            .collect(Collectors.toList());

                    question.setAnswers(answers);
                    return question;
                })
                .collect(Collectors.toList());

        quiz.setQuizQuestions(quizQuestions);
        quizRepository.save(quiz); // cascade sẽ lưu hết cả question và answer
    }
    public void updateFullQuiz(UpdateFullQuizRequest request) {
        Quiz quiz = quizRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Cập nhật thông tin chung
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCourse(course);

        // Xóa hết các câu hỏi cũ
        quiz.getQuizQuestions().clear(); // Đảm bảo orphanRemoval sẽ xoá ở DB

        // Thêm danh sách câu hỏi mới
        for (UpdateFullQuizRequest.UpdateQuestion q : request.getQuestions()) {
            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .content(q.getContent())
                    .build();

            List<QuestionAnswer> answers = q.getAnswers().stream()
                    .map(a -> QuestionAnswer.builder()
                            .question(question)
                            .content(a.getContent())
                            .isCorrect(a.getIsCorrect())
                            .build())
                    .collect(Collectors.toList());

            question.setAnswers(answers);
            quiz.getQuizQuestions().add(question);
        }

        quizRepository.save(quiz); // Cascade sẽ tự lưu questions và answers
    }




    public QuizByIdResponse getQuizById(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        return mapToResponse(quiz);
    }
    public List<QuizByIdResponse> getQuizzesForInstructor(Long courseId){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        List<Quiz> quizzes = quizRepository.findQuizByIsDeletedFalseAndCourseId(courseId);
        return quizzes.stream().map(this::mapToResponse).toList();
    }
    private QuizByIdResponse mapToResponse(Quiz quiz) {
        return QuizByIdResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .courseId(quiz.getCourse().getId())
                .createdAt(quiz.getCreatedAt())
                .questions(
                        quiz.getQuizQuestions().stream().map(q ->
                                QuizByIdResponse.QuestionResponse.builder()
                                        .id(q.getId())
                                        .content(q.getContent())
                                        .answers(
                                                q.getAnswers().stream().map(a ->
                                                        QuizByIdResponse.AnswerResponse.builder()
                                                                .id(a.getId())
                                                                .content(a.getContent())
                                                                .isCorrect(a.getIsCorrect())
                                                                .build()
                                                ).collect(Collectors.toList())
                                        )
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }
}
