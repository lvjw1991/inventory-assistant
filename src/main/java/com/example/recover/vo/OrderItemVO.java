package com.example.recover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVO {

    private Long id;

    private String supplierCode;

    private String productName;

    private String barcode;

    private Integer orderQty;

    private Integer actualQty;

    private Integer total;

    private String expiryDate;

    private BigDecimal unitPrice;

    private String category;

    @Schema(description = "含糖等级", example = "A,B,C,D,E")
    private String sugar;

    @Schema(description = "是否正确", example = "UNCHECKED,PASS,FAIL")
    private String checkStatus;

    private Integer damageQty;

    private String remark;

    private List<String> damageImgList;

    private Integer cartonQty;

    private Integer otherBoxQty;

}
