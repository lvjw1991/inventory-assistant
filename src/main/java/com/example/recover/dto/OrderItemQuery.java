package com.example.recover.dto;

import com.example.recover.utils.CheckStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "收货单明细分页查询参数")
public class OrderItemQuery {

    @NotNull
    private Long orderId;

    private String productName;

    private String supplierCode;

    private CheckStatus checkStatus;

    @Schema(description = "页码，从0开始", example = "0")
    private Integer pageNum = 0;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

}
