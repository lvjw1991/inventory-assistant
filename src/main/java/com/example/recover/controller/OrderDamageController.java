package com.example.recover.controller;

import com.example.recover.service.OrderDamageService;
import com.example.recover.vo.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "破损管理")
@RestController
@RequestMapping("/api/damages")
@RequiredArgsConstructor
public class OrderDamageController {

    private final OrderDamageService orderDamageService;

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public Result<String> importImg(@RequestParam("file") MultipartFile file) {
        return orderDamageService.importImg(file);
    }

}
