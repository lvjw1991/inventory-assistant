package com.example.recover.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import jakarta.validation.constraints.NotNull;


@Data
public class StockRequest {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Long quantity;
}
