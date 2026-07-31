package com.example.recover.service;

import com.example.recover.dto.PageResponse;
import com.example.recover.dto.ProductRequest;
import com.example.recover.dto.Result;
import com.example.recover.entity.Product;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Result<PageResponse<Product>> findAllByPage(int pageNum, int pageSize) {
        return Result.success(PageResponse.of(productRepository.findAll(PageRequest.of(pageNum,pageSize))));
    }

    public Result<Product> findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(product);
    }

    @Transactional
    public Result<Product> create(ProductRequest productRequest) {
        if(productRepository.existsByBarcode(productRequest.getBarcode())){
            return Result.fail(500,"barcode已存在");
        }
        Product product = new Product();
        product.setBarcode(productRequest.getBarcode());
        product.setName(productRequest.getName());
        product.setImgUrl(productRequest.getImgUrl());
        product.setCategory(productRequest.getCategory());
        product.setStatus(true);
        return Result.success(productRepository.save(product));
    }

    @Transactional
    public Result<Product> update(ProductRequest productRequest) {
        Product product = findById(productRequest.getId()).getData();
        Product byBarcode = productRepository.findByBarcode(productRequest.getBarcode());
        if(byBarcode!=null && !byBarcode.getId().equals(product.getId()) ){
            return Result.fail(500,"barcode已存在");
        }
        product.setBarcode(productRequest.getBarcode());
        product.setName(productRequest.getName());
        product.setImgUrl(productRequest.getImgUrl());
        product.setCategory(productRequest.getCategory());
        return Result.success(productRepository.save(product));
    }


    public Result<Boolean> delete(Long id) {
        Product product = findById(id).getData();
        productRepository.delete(product);
        return Result.success(true);
    }
}
