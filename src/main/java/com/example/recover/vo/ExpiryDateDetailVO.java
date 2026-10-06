package com.example.recover.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiryDateDetailVO {


    private String barcode;

    private String productName;

    private String imgUrl;

    private List<LocalDate> allDateList;
}
