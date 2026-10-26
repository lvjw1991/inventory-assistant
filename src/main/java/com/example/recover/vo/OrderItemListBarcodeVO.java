package com.example.recover.vo;

import com.example.recover.utils.CheckStatus;
import com.example.recover.utils.OrderProcess;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemListBarcodeVO {

    private String supplierName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

    private OrderProcess progress;

    private Long id;

    private String productName;

    private Integer orderQty;

    private Integer total;

    private String barcode;

    private String expiryDate;

    private CheckStatus checkStatus;

}
