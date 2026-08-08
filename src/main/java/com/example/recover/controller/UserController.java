package com.example.recover.controller;

import com.example.recover.dto.UserDto;
import com.example.recover.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
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

