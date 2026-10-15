package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BarcodeNamePriceRow {

    @ExcelProperty("NUMERO")
    private String barcode;

    @ExcelProperty("NIMI")
    private String name;

    @ExcelProperty("KESKIHINTA")
    private BigDecimal price;

}
