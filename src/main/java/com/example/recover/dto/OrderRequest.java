package com.example.recover.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderRequest {

    private Long id;

    @NotNull
    @Schema(description = "供应商ID", example = "4")
    private Long supplierId;

    @Schema(description = "number", example = "1")
    private Integer number;

    @Schema(description = "收货日期", example = "2026-08-08")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

    @Schema(description = "温度", example = "-18")
    private String temperature;

    @Schema(description = "运输公司", example = "flower truck")
    private String transport;
}
