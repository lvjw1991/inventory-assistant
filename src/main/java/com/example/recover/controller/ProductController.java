package com.example.recover.controller;

import com.example.recover.vo.PageResponse;
import com.example.recover.dto.ProductRequest;
import com.example.recover.vo.Result;
import com.example.recover.entity.Product;
import com.example.recover.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<Product>> search(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return productService.findAllByPage(pageNum, pageSize);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    /**
     * 创建
     */
    @PostMapping
    public Result<Product> create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        request.setId(id);
        return productService.update(request);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
