package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.event.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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


    @KafkaListener(topics = "register", groupId = "notification-group")
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
    @KafkaListener(topics = "reset-password", groupId = "notification-group")
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
}
