package com.example.recover.dto;

import com.example.recover.utils.CheckStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemCheckRequest {

    @NotBlank(message = "barcode不能为空")
    private String barcode;
    private Integer actualQty;
    private List<String> expiryDate;
    private String sugar;

    @NotNull(message = "status不能为空")
    private CheckStatus status;


}
