package com.example.recover.service;

import com.example.recover.dto.BarcodeNameImgRow;
import com.example.recover.utils.ExcelUtils;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.dto.ProductRequest;
import com.example.recover.vo.Result;
import com.example.recover.entity.Product;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Result<PageResponse<Product>> findAllByPage(int pageNum, int pageSize) {
        return Result.success(PageResponse.of(productRepository.findAll(PageRequest.of(pageNum, pageSize))));
    }

    public Result<Product> findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(product);
    }

    @Transactional
    public Result<Product> create(ProductRequest productRequest) {
        if (productRepository.existsByBarcode(productRequest.getBarcode())) {
            return Result.fail(500, "barcode已存在");
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
        if (byBarcode != null && !byBarcode.getId().equals(product.getId())) {
            return Result.fail(500, "barcode已存在");
        }
        product.setBarcode(productRequest.getBarcode());
        product.setName(productRequest.getName());
        product.setImgUrl(productRequest.getImgUrl());
        product.setCategory(productRequest.getCategory());
        return Result.success(productRepository.save(product));
    }

    @Transactional
    public Result<Boolean> delete(Long id) {
        Product product = findById(id).getData();
        productRepository.delete(product);
        return Result.success(true);
    }

    @Transactional
    public Result<ImportResultVO> importExcel(MultipartFile file) throws IOException {
        List<BarcodeNameImgRow> rowList = ExcelUtils.read(file, BarcodeNameImgRow.class);
        ImportResultVO result = dealWith(rowList);
        return Result.success(result);
    }

    public ImportResultVO dealWith(List<BarcodeNameImgRow> rowList) {
        List<Product> all = productRepository.findAll();
        Map<String, Product> productMap = all.stream()
                .collect(Collectors.toMap(Product::getBarcode, Function.identity()));
        List<Product> toInsertList = new ArrayList<>();
        List<Product> toUpdateList = new ArrayList<>();
        int success = 0, skip = 0;
        for (BarcodeNameImgRow row : rowList) {
            Product existing = productMap.get(row.getGtin());
            if (existing == null) {
                // new
                if(StringUtils.isNotBlank(row.getGtin()) && StringUtils.isNotBlank(row.getName())
                        && StringUtils.isNotBlank(row.getImages())){
                    Product toInsert = new Product();
                    toInsert.setBarcode(row.getGtin());
                    toInsert.setName(row.getName());
                    toInsert.setImgUrl(row.getImages());
                    toInsertList.add(toInsert);
                }
            } else if (!Objects.equals(existing.getName(), row.getName())
                    || !Objects.equals(existing.getImgUrl(), row.getImages())) {
                // update
                existing.setBarcode(row.getGtin());
                existing.setName(row.getName());
                existing.setImgUrl(row.getImages());
                toUpdateList.add(existing);
            }
        }
        productRepository.saveAll(toInsertList);
        productRepository.saveAll(toUpdateList);
        success = toInsertList.size() + toUpdateList.size();
        skip = rowList.size() - success;
        return new ImportResultVO(success, skip);
    }
}
