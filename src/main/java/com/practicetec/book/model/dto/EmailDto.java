package com.practicetec.book.model.dto;

public record EmailDto(
        String to,
        String username,
        EmailTemplateName emailTemplateName,
        String confirmationUrl,
        String activationCode,
        String subject
) {
}
