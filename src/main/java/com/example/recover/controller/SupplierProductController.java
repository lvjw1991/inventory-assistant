package com.example.recover.controller;

import com.example.recover.entity.SupplierProduct;
import com.example.recover.service.SupplierProductService;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class SupplierProductController {

    private final SupplierProductService supplierProductService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<SupplierProduct>> search(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return supplierProductService.findAllByPage(pageNum, pageSize, supplierId);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<SupplierProduct> getById(@PathVariable Long id) {
        return supplierProductService.findById(id);
    }


}
