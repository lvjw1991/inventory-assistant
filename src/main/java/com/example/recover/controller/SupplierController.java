package com.example.recover.controller;

import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import com.example.recover.dto.SupplierRequest;
import com.example.recover.entity.Supplier;
import com.example.recover.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * 查询全部供应商
     */
    @GetMapping
    public Result<PageResponse<Supplier>> search(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return supplierService.findAllByPage(pageNum, pageSize);
    }

    /**
     * 查询单个供应商
     */
    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Long id) {
        return supplierService.findById(id);
    }

    /**
     * 创建供应商
     */
    @PostMapping
    public Result<Supplier> create(@Valid @RequestBody SupplierRequest supplierRequest) {
        return supplierService.create(supplierRequest);
    }

    /**
     * 修改供应商
     */
    @PutMapping("/{id}")
    public Result<Supplier> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest supplierRequest) {
        supplierRequest.setId(id);
        return supplierService.update(supplierRequest);
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return supplierService.delete(id);
    }
}
