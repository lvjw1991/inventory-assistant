package com.example.recover.controller;

import com.example.recover.dto.OrderRequest;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import com.example.recover.entity.ReceivingOrder;
import com.example.recover.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<ReceivingOrder>> search(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.findAllByPage(pageNum, pageSize);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<ReceivingOrder> getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    /**
     * 创建
     */
    @PostMapping
    public Result<ReceivingOrder> create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<ReceivingOrder> update(@Valid @RequestBody OrderRequest request) {
        return orderService.update(request);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return orderService.delete(id);
    }


    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file,
                                              @RequestParam("orderId") Long orderId) throws IOException {
        return orderService.importExcel(file, orderId);
    }
}
