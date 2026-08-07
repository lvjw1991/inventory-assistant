package com.example.recover.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    private Long id;

    @NotNull
    private Long supplierId;
    private String invoiceNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String receiveDate;


}
