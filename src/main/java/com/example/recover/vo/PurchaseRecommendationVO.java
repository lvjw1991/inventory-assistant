package com.example.recover.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseRecommendationVO {

    private Long productId;
    private String barcode;
    private String productName;
    private String category;
    private String imgUrl;
    private BigDecimal purchasePrice;
    private BigDecimal stock;
    private BigDecimal avgMonthlySalesQty;
    private BigDecimal stockMonth;
    private List<SupplierOptionVO> suppliers;
}
