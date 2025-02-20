package com.practicetec.book.service.auth;

import com.practicetec.book.model.dto.EmailDto;
import com.practicetec.book.model.dto.EmailTemplateName;
import com.practicetec.book.model.entity.Role;
import com.practicetec.book.model.entity.Token;
import com.practicetec.book.model.entity.User;
import com.practicetec.book.model.mapper.UserMapper;
import com.practicetec.book.model.request.auth.AuthenticacionRequest;
import com.practicetec.book.model.request.auth.UserRequest;
import com.practicetec.book.model.response.auth.AuthenticationResponse;
import com.practicetec.book.persistence.RoleRepository;
import com.practicetec.book.persistence.TokenRepository;
import com.practicetec.book.persistence.UserRepository;
import com.practicetec.book.service.email.EmailService;
import com.practicetec.book.service.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.security.code-verify:6}")
    private int LENGHT_CODE_VERIFY;
    @Value("${application.security.minutes-expired-activation}")
    private int MINUTES_EXPIRED_ACTIVATIOM;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    public void register(UserRequest request) throws MessagingException {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        String password = passwordEncoder.encode(request.getPassword());
        User user = UserMapper.toUser(request);
        user.setRoles(Set.of(userRole));
        user.setPassword(password);

        userRepository.save(user);
        sendValidationEmail(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticacionRequest authenticationRequest) {
        //si pasa auth es que está en la bd y sus datos son correctod
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        HashMap<String, Object> claims = new HashMap<>();
        User user = ((User) auth.getPrincipal());
        claims.put("fullname", user.fullName());
        String jwtGenerate = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .jwt_token(jwtGenerate)
                .build();
    }

    @Override
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new MessagingException("Activation token has expired. A new token has been send to the same email address");
        }
        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() ->new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationTocken(user);
        //sendEmail
        EmailDto emailDto = new EmailDto(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.AVTIVATION_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation");
        emailService.sendEmail(emailDto);
    }

    private String generateAndSaveActivationTocken(User user) {
        String generatedToken = generateActivationCode(LENGHT_CODE_VERIFY);
        Token token = Token.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(MINUTES_EXPIRED_ACTIVATIOM))
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int lenght){
        String chareacter = "0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < lenght; i++) {
            int randomIndex = secureRandom.nextInt(chareacter.length());
            stringBuilder.append(chareacter.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }


}
