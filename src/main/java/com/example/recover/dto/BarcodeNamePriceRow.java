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

    @ExcelProperty("HINTA")
    private BigDecimal salePrice;

    @ExcelProperty("NIMI2")
    private String nameFi;

    @ExcelProperty("hryksikko")
    private String netUnit;

    @ExcelProperty("HRKERROIN")
    private BigDecimal netContent;
}
