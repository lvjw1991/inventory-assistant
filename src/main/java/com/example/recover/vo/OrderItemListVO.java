package com.example.recover.vo;

import lombok.Data;

@Data
public class OrderItemListVO {

    private Long id;

    private String supplierCode;

    private String productName;

    private Integer orderQty;

    private Integer total;

    private String barcode;

    private String expiryDate;

    private String checkStatus;

}
