package com.practicetec.book.model.mapper;

import com.practicetec.book.model.entity.Role;
import com.practicetec.book.model.entity.User;
import com.practicetec.book.model.request.auth.UserRequest;

import java.util.HashSet;
import java.util.Set;


public class UserMapper {
    public static User toUser(UserRequest userRequest) {
        if(userRequest == null) {
            RuntimeException runtimeException = new RuntimeException("UserRequest is null");
        }
        Set<Role> roles = new HashSet<>();
        assert userRequest != null;
        return User.builder()
                .firstname(userRequest.getFirstname())
                .lastname(userRequest.getLastname())
                .email(userRequest.getEmail())
                .password("")
                .accountLocked(false)
                .enabled(false)
                .roles(roles)
                .build();
    }
}
