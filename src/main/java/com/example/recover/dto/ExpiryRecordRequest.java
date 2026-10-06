package com.example.recover.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpiryRecordRequest {

    private Long id;

    @NotBlank
    private String barcode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate expiryDate;

    private String category;

    private String productName;


}
