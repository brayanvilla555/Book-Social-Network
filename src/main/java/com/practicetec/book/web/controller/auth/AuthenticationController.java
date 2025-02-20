package com.practicetec.book.web.controller.auth;


import com.practicetec.book.model.request.auth.AuthenticacionRequest;
import com.practicetec.book.model.request.auth.UserRequest;
import com.practicetec.book.model.response.auth.AuthenticationResponse;
import com.practicetec.book.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest request) throws MessagingException {
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticacionRequest authenticacionRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticacionRequest));
    }

    @GetMapping("/activate-account")
    public void activateAccount(@RequestParam String token) throws MessagingException {
        authenticationService.activateAccount(token);
    }
}
