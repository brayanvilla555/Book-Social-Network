package com.practicetec.book.service.auth;

import com.practicetec.book.model.request.auth.AuthenticacionRequest;
import com.practicetec.book.model.request.auth.UserRequest;
import com.practicetec.book.model.response.auth.AuthenticationResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface AuthenticationService {
    void register(@Valid UserRequest request) throws MessagingException;

    AuthenticationResponse authenticate(@Valid AuthenticacionRequest authenticacionRequest);

    void activateAccount(String code) throws MessagingException;
}
