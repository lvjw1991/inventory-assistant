package com.example.recover.vo;

import com.example.recover.utils.OrderProcess;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收货单列表")
public class ReceivingOrderVO {

    @Schema(description = "收货单ID", example = "6")
    private Long id;

    @Schema(description = "供应商ID", example = "4")
    private Long supplierId;

    @Schema(description = "number", example = "1")
    private Integer number;

    @Schema(description = "收货日期", example = "2026-08-08")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

    @Schema(description = "货单状态", example = "DRAFT,READY,CHECKING,COMPLETED")
    private OrderProcess progress;

    @Schema(description = "温度", example = "-18")
    private String temperature;

    @Schema(description = "运输公司", example = "flower truck")
    private String transport;

    @Schema(description = "供应商名称", example = "K-FOOD")
    private String supplierName;

}
