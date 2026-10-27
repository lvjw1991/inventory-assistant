package com.example.recover.controller;

import com.example.recover.dto.ProductCategoryBatchRequest;
import com.example.recover.utils.CategoryStatus;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.dto.ProductRequest;
import com.example.recover.vo.ProductCategoryItemVO;
import com.example.recover.vo.Result;
import com.example.recover.entity.Product;
import com.example.recover.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * 导入wolt图片
     */
    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return productService.importExcel(file);
    }

    /**
     * 导入pos价格
     */
    @PostMapping("/import-price")
    public Result<ImportResultVO> importPrice(@RequestParam("file") MultipartFile file) throws IOException {
        return productService.importPrice(file);
    }

    @GetMapping("/category-management")
    public Result<PageResponse<ProductCategoryItemVO>> query(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "UNCLASSIFIED")
            CategoryStatus categoryStatus
    ) {
        return Result.success(productService.query(
                pageNum,
                pageSize,
                keyword,
                categoryStatus
        ));
    }

    @PatchMapping("/category/batch")
    public Result<Boolean> batchUpdate(
            @Valid @RequestBody ProductCategoryBatchRequest request
    ) {
        productService.batchUpdate(request);
        return Result.success(true);
    }

}
