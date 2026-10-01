package com.example.recover.vo;

import lombok.Data;

@Data
public class OrderItemListMobileVO {

    private Long id;

    private String supplierCode;

    private String productName;

    private Integer orderQty;

    private String barcode;

    private String expiryDate;

    private String checkStatus;

    private String category;

}
