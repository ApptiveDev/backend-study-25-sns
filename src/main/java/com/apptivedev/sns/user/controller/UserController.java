package com.apptivedev.sns.user.controller;

import com.apptivedev.sns.auth.interceptor.AuthInterceptor;
import com.apptivedev.sns.user.dto.*;
import com.apptivedev.sns.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }
    @GetMapping("/{userId}")
    public UserResponse getUser(@RequestAttribute(AuthInterceptor.USER_ID) Long loginId, @PathVariable("userId") Long userId) {
        return userService.getUser(loginId, userId);
    }
    @PatchMapping("/{userId}")
    public UserResponse updateUser(@RequestAttribute(AuthInterceptor.USER_ID) Long loginId, @PathVariable("userId") Long userId,
                                   @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(loginId, userId, request);
    }
    @DeleteMapping("/{userId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestAttribute(AuthInterceptor.USER_ID) Long loginId, @PathVariable("userId") Long userId) {
        userService.deleteUser(loginId, userId);
    }
}
