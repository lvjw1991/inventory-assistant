package com.example.recover.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static java.time.temporal.WeekFields.ISO;


@Data
@Schema(description = "采购入库请求")
public class PurchaseRequest {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    @Schema(description = "数量")
    private Long quantity;

    @Schema(description = "进价")
    private BigDecimal costPrice;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "BBD")
    private LocalDate expiredDate;
}
