package com.example.recover.controller;

import com.example.recover.dto.LoginRequest;
import com.example.recover.dto.RegisterRequest;
import com.example.recover.dto.UserDto;
import com.example.recover.service.UserService;
import com.example.recover.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "查询所有用户")
    public List<UserDto> findAll(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public UserDto getById(@Parameter(description = "用户ID")@PathVariable Long id){
        return userService.getById(id);
    }
}

