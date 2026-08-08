package com.example.recover.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "收货单分页查询参数")
public class OrderQuery {

    @Schema(description = "供应商ID", example = "4")
    private Long supplierId;

    @Schema(description = "开始日期", example = "2026-08-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-08-08")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "页码，从0开始", example = "0")
    private Integer pageNum = 0;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

}
