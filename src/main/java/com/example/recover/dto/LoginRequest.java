package com.example.recover.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "用户名称不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
