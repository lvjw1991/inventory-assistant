package com.example.recover.dto;

import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ProcessStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RecordQuery {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDateTo;
    private ConfirmStatus confirmStatus;      // 0未确认 1已确认 null=全部
    private ProcessStatus processStatus;
    private String category;          // 产品类型
    private String barcode;
    private int pageNum = 0;
    private int pageSize = 20;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDateTo;

}
