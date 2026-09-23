package com.example.recover.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BarcodeNameImgRow {

    @ExcelProperty("gtin")
    private String gtin;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("images")
    private String images;

}
