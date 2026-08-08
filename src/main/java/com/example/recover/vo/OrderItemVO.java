package com.example.recover.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long id;

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
    @Schema(description = "含糖等级", example = "A,B,C,D,E")
    private String sugar;

    @ExcelProperty("isCorrect")
    @Schema(description = "是否正确", example = "UNCHECKED,PASS,FAIL")
    private String checkStatus;


}
