package com.practicetec.book.service.email;

import com.practicetec.book.model.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Async
    public void sendEmail(EmailDto emailDto) throws MessagingException {
        String templateName;
        if(emailDto.emailTemplateName() == null){
            templateName = "confirm-email";
        }else {
            templateName = emailDto.emailTemplateName().getName();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", emailDto.username());
        properties.put("confirmationUrl", emailDto.confirmationUrl());
        properties.put("activation_code", emailDto.activationCode());

        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom("brayanalexander098@gmail.com");
        mimeMessageHelper.setTo(emailDto.to());
        mimeMessageHelper.setSubject(emailDto.subject());

        String template = springTemplateEngine.process(templateName, context);
        mimeMessageHelper.setText(template, true);
        mailSender.send(mimeMessage);
    }

}
