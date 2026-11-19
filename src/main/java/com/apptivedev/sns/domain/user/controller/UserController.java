package com.apptivedev.sns.domain.user.controller;

import com.apptivedev.sns.domain.auth.interceptor.AuthInterceptor;
import com.apptivedev.sns.domain.user.dto.UpdateUserRequest;
import com.apptivedev.sns.domain.user.dto.UserResponse;
import com.apptivedev.sns.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUser(
            @RequestAttribute(AuthInterceptor.LOGIN_USER_ID) Long loginUserId,
            @PathVariable("userId") Long userId
    ) {
        return userService.getUser(loginUserId, userId);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(
            @RequestAttribute(AuthInterceptor.LOGIN_USER_ID) Long loginUserId,
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(loginUserId, userId, request);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @RequestAttribute(AuthInterceptor.LOGIN_USER_ID) Long loginUserId,
            @PathVariable("userId") Long userId
    ) {
        userService.deleteUser(loginUserId, userId);

        return ResponseEntity.noContent().build();
    }
}