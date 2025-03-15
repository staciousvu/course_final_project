package com.example.courseapplicationproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @RabbitListener(queues = "${rabbitmq.queue-name}")
    public void sendOTPEmail(MessageRabbbitMQ messageRabbbitMQ) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Sử dụng Thymeleaf để render nội dung HTML
        Context context = new Context();
        context.setVariable("otp", messageRabbbitMQ.getContent());
        context.setVariable("companyName", messageRabbbitMQ.getSubject());
        String htmlContent = templateEngine.process(messageRabbbitMQ.getTemplateHTML(), context);

        helper.setTo(messageRabbbitMQ.getRecipient());
        helper.setSubject(messageRabbbitMQ.getSubject());
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}
