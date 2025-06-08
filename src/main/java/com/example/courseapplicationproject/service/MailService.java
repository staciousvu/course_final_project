package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.event.NotificationEmailTemplateData;
import com.example.courseapplicationproject.dto.event.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.courseapplicationproject.dto.event.MessageRabbbitMQ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    @Async
//    @KafkaListener(topics = "register", groupId = "notification-group")
    public void register(NotificationEvent notificationEvent) throws MessagingException {
        log.info("Received Kafka message: {}", notificationEvent);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariables(notificationEvent.getParam());
        String htmlContent = templateEngine.process(notificationEvent.getTemplateCode(), context);

        helper.setTo(notificationEvent.getRecipient());
        helper.setSubject(notificationEvent.getSubject());
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
        log.info("Email sent to: {}", notificationEvent.getRecipient());
    }

    @Async
//    @KafkaListener(topics = "reset-password", groupId = "notification-group")
    public void resetPassword(NotificationEvent notificationEvent) throws MessagingException {
        log.info("Received Kafka reset password event: {}", notificationEvent);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariables(notificationEvent.getParam());
        String htmlContent = templateEngine.process(notificationEvent.getTemplateCode(), context);

        helper.setTo(notificationEvent.getRecipient());
        helper.setSubject(notificationEvent.getSubject());
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
        log.info("Reset password email sent to: {}", notificationEvent.getRecipient());
    }
    @Async
    public void notification(NotificationEmailTemplateData emailData) throws MessagingException {
        log.info("Received email notification: {}", emailData);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Chuẩn bị dữ liệu cho Thymeleaf
        Context context = new Context();
        context.setVariable("messageTitle", emailData.getMessageTitle());
        context.setVariable("messageBody", emailData.getMessageBody());
        context.setVariable("actionLabel", emailData.getActionLabel());
        context.setVariable("actionUrl", emailData.getActionUrl());
        context.setVariable("courseImage", emailData.getCourseImage());
        context.setVariable("companyName", emailData.getCompanyName());

        // Render HTML từ template
        String htmlContent = templateEngine.process("notification-email-template", context);

        // Cấu hình email
        helper.setTo(emailData.getRecipient());
        helper.setSubject(emailData.getMessageTitle()); // Subject = Title
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
        log.info("Email sent to: {}", emailData.getRecipient());
    }
    @Async
    public void remove_course(NotificationEmailTemplateData emailData) throws MessagingException {
        log.info("Received email notification: {}", emailData);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Chuẩn bị dữ liệu cho Thymeleaf
        Context context = new Context();
        context.setVariable("messageTitle", emailData.getMessageTitle());
        context.setVariable("messageBody", emailData.getMessageBody());
        context.setVariable("actionLabel", emailData.getActionLabel());
        context.setVariable("actionUrl", emailData.getActionUrl());
        context.setVariable("courseImage", emailData.getCourseImage());
        context.setVariable("companyName", emailData.getCompanyName());
        context.setVariable("reason", emailData.getReason());

        // Render HTML từ template
        String htmlContent = templateEngine.process(emailData.getTemplate(), context);

        // Cấu hình email
        helper.setTo(emailData.getRecipient());
        helper.setSubject(emailData.getMessageTitle()); // Subject = Title
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
        log.info("Email sent to: {}", emailData.getRecipient());
    }

}
