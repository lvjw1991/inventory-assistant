package com.example.recover.dto;

import com.example.recover.utils.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private UserRole role;
    private String token;
}
