package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryStockRow {

    @ExcelProperty("barcode")
    private String barcode;

    @ExcelProperty("stock")
    private BigDecimal stock;

}
