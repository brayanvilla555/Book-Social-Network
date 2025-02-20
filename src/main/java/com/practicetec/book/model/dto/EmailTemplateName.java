package com.practicetec.book.model.dto;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    AVTIVATION_ACCOUNT("activate_account");

    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
