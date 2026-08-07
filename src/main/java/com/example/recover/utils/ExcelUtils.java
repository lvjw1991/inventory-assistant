package com.example.recover.utils;

import com.alibaba.excel.EasyExcel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ExcelUtils {

    public static <T> List<T> read(MultipartFile file, Class<T> clazz) throws IOException {
        return EasyExcel
                .read(file.getInputStream())
                .head(clazz)
                .sheet()
                .doReadSync();
    }
}
