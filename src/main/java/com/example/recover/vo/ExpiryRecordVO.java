package com.example.recover.vo;

import com.example.recover.utils.ProcessMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpiryRecordVO {

    private Long id;

    private String barcode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Integer stock;

    private Boolean confirmStatus;

    private LocalDateTime confirmTime;

    private Boolean processStatus;

    @Enumerated(EnumType.STRING)
    private ProcessMethod processMethod;

    private LocalDateTime processTime;

    private String processRemark;

    private String category;

    private String productName;

    private String imgUrl;


}
