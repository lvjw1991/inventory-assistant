package com.example.recover.controller;

import com.example.recover.dto.OrderQuery;
import com.example.recover.dto.OrderRequest;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.ReceivingOrderVO;
import com.example.recover.vo.Result;
import com.example.recover.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "收货单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询全部
     */
    @Operation(
            summary = "分页查询收货单",
            description = "根据供应商ID、收货日期范围进行分页查询"
    )
    @GetMapping
    public Result<PageResponse<ReceivingOrderVO>> search(@ParameterObject OrderQuery orderQuery) {
        return orderService.findAllByPage(orderQuery);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<ReceivingOrderVO> getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    /**
     * 创建
     */
    @PostMapping
    public Result<ReceivingOrderVO> create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<ReceivingOrderVO> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        request.setId(id);
        return orderService.update(request);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return orderService.delete(id);
    }


    /**
     * 货单导入
     * @param file
     * @param orderId
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file,
                                              @RequestParam("orderId") Long orderId) throws IOException {
        return orderService.importExcel(file, orderId);
    }

    /**
     * 点货完成
     * @param id orderId
     * @return
     */
    @PostMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        return orderService.complete(id);
    }
}
