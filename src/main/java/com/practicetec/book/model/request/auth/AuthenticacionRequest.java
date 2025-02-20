package com.practicetec.book.model.request.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticacionRequest {
    @Email(message = "Email is no well formatted")
    @NotNull(message = "Email is mandatory")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Password is mandatory")
    @NotEmpty(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimun")
    private String password;
}
