package com.example.recover.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportResultVO {
    private int success;  // 导入成功数
    private int skip;     // 跳过数（条码重复）
}
