package com.example.recover.controller;

import com.example.recover.dto.PriceTagRequest;
import com.example.recover.entity.Product;
import com.example.recover.service.OrderService;
import com.example.recover.service.ProductService;
import com.example.recover.vo.PriceTagVO;
import com.example.recover.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class PriceTagController {

    private final ProductService productService;

    private final OrderService orderService;

    @GetMapping("/orders/{orderId}/items")
    public Result<List<Product>> findProductListByOrderId(@PathVariable Long orderId) {
        return orderService.findProductListByOrderId(orderId);
    }

    @PostMapping("/items")
    public Result<List<PriceTagVO>> findTagList(@RequestBody List<Long> idList) {
        return productService.findTagList(idList);
    }

    @PutMapping("/items/{id}")
    public Result<PriceTagVO> update(@PathVariable Long id, @Valid @RequestBody PriceTagRequest request) {
        request.setId(id);
        return productService.updatePriceTag(request);
    }
}
