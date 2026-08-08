package com.example.recover.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecordQuery {

    private LocalDate expireDateFrom;
    private LocalDate expireDateTo;
    private Boolean isConfirmed;      // 0未确认 1已确认 null=全部
    private Boolean isProcessed;
    private String category;          // 产品类型
    private String barcode;
    private int pageNum = 0;
    private int pageSize = 20;

}
