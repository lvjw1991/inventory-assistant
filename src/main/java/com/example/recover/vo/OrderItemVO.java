package com.example.recover.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    @ExcelProperty("supplierCode")
    private String supplierCode;

    @ExcelProperty("productName")
    private String productName;

    @ExcelProperty("barcode")
    private String barcode;

    @ExcelProperty("orderQty")
    private Integer orderQty;

    @ExcelProperty("actualQty")
    private Integer actualQty;

    @ExcelProperty("total")
    private Integer total;

    @ExcelProperty("expiryDate")
    private String expiryDate;

    @ExcelProperty("unitPrice")
    private BigDecimal unitPrice;

    @ExcelProperty("category")
    private String category;

    @ExcelProperty("sugar")
    private String sugar;

    @ExcelProperty("isCorrect")
    private String checkStatus;


}
