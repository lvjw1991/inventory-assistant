package com.example.recover.dto;

import java.math.BigDecimal;

public interface PurchaseRecommendationProjection {

    Long getProductId();

    String getBarcode();

    String getProductName();

    String getCategory();

    String getImgUrl();

    BigDecimal getPurchasePrice();

    BigDecimal getStock();

    BigDecimal getAvgMonthlySalesQty();
}
