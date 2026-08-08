package com.example.recover.dto;

import com.example.recover.utils.CheckStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemRequest {

    private Long id;

    private Long receivingOrderId;
    private String supplierCode;
    private String productName;
    private String barcode;
    private Integer orderQty;
    private Integer actualQty;
    private Integer total;
    private List<String> expiryDate;
    private BigDecimal unitPrice;
    private String category;
    private String sugar;
    @Enumerated(EnumType.STRING)
    private CheckStatus checkStatus;

}
