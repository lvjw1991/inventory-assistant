package com.example.recover.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 通用响应结构（成功 + 失败统一格式）
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
