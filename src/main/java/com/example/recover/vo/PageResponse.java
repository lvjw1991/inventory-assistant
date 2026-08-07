package com.example.recover.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> list;        // 当前页数据
    private long total;          // 总记录数
    private int pageNum;         // 当前页（从1开始，前端友好）
    private int pageSize;        // 每页大小
    private int totalPages;      // 总页数
    private boolean hasNext;     // 是否有下一页

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,   // 转为从1开始
                page.getSize(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
