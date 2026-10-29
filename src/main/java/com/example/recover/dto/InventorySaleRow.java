package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventorySaleRow {

    @ExcelProperty("barcode")
    private String barcode;

    @ExcelProperty("avgMonthlySalesQty")
    private BigDecimal avgMonthlySalesQty;

}
