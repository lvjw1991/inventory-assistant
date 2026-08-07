package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRow {

    @ExcelProperty("code")
    private String supplierCode;

    @ExcelProperty("name")
    private String productName;

    @ExcelProperty("num")
    private Integer orderQty;

    @ExcelProperty("total")
    private Integer total;

    @ExcelProperty("unitPrice")
    private BigDecimal unitPrice;

    @ExcelProperty("type")
    private String category;
}
