package com.example.recover.controller;

import com.example.recover.dto.LoginRequest;
import com.example.recover.dto.RegisterRequest;
import com.example.recover.vo.Result;
import com.example.recover.dto.UserDto;
import com.example.recover.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "登录注册")
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")  // 仅管理员可注册用户
    public Result<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest){
        return userService.register(registerRequest);
    }

    @PostMapping("/login")
    public Result<UserDto> login(@Valid @RequestBody LoginRequest loginRequest){
        return userService.login(loginRequest);
    }

    @GetMapping("/me")
    public Result<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getCurrentUser(userDetails.getUsername());
    }

}

