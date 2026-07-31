package com.example.recover.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {

    private Long id;
    @NotBlank(message = "barcode不能为空")
    private String barcode;
    @NotBlank(message = "Name不能为空")
    private String name;

    private String imgUrl;
    private String category;

}
