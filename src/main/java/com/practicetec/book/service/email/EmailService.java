package com.practicetec.book.service.email;

import com.practicetec.book.model.dto.EmailDto;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(EmailDto emailDto) throws MessagingException;
}
