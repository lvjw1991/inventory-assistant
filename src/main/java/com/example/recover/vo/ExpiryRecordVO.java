package com.example.recover.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "处理方式", example = "NORMAL,PROMOTE,DAMAGE")
    private String processMethod;

    private LocalDateTime processTime;

    private String processRemark;

    private String category;

    private String productName;

    private String imgUrl;


}
