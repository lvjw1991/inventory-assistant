package com.example.recover.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "根据条形码查询货单分页查询参数")
public class OrderItemBarcodeQuery {

    @NotBlank(message = "barcode不能为空")
    private String barcode;

    @Schema(description = "页码，从0开始", example = "0")
    private Integer pageNum = 0;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

}
