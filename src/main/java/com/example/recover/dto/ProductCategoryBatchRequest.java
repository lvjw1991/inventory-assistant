package com.example.recover.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductCategoryBatchRequest {

    @NotEmpty
    @Size(max = 1000)
    private List<Long> productIds;

    @NotBlank
    private String category;

}
