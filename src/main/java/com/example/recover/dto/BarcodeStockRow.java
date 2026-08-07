package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BarcodeStockRow {

    @ExcelProperty("barcode")
    private String barcode;

    @ExcelProperty("stock")
    private Integer stock;

}
