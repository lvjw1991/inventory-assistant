package com.example.recover.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemExportVO {

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

    @ExcelProperty("category")
    private String category;

    @ExcelProperty("sugar")
    private String sugar;

    @ExcelProperty("isCorrect")
    private String checkStatus;

    @ExcelProperty("damageQty")
    private Integer damageQty;

    @ExcelProperty("remark")
    private String remark;

    @ExcelProperty("cartonQty")
    private Integer cartonQty;

    @ExcelProperty("unitPrice")
    private BigDecimal unitPrice;

    @ExcelProperty("posPurchasePrice")
    private BigDecimal posPurchasePrice;

    @ExcelProperty("priceDiff")
    private BigDecimal priceDiff;

    @ExcelProperty("result")
    private String result;

    @ExcelProperty("otherBoxQty")
    private Integer otherBoxQty;

}
