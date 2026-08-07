package com.example.recover.service;

import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import com.example.recover.dto.SupplierProductRequest;
import com.example.recover.entity.SupplierProduct;
import com.example.recover.repository.SupplierProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierProductService {

    private final SupplierProductRepository supplierProductRepository;

    /**
     * 录入时同时维护供应商code和barcode关系
     * @param supplierProductRequest
     * @return
     */
    @Transactional
    public Result<Boolean> save(SupplierProductRequest supplierProductRequest) {
        Long supplierId = supplierProductRequest.getSupplierId();
        String supplierCode = supplierProductRequest.getSupplierCode();
        String barcode = supplierProductRequest.getBarcode();
        boolean exists = supplierProductRepository.existsBySupplierIdAndSupplierCodeAndBarcode(supplierId,
                supplierCode, barcode);
        if (!exists) {
            SupplierProduct supplierProduct = new SupplierProduct();
            supplierProduct.setSupplierId(supplierId);
            supplierProduct.setSupplierCode(supplierCode);
            supplierProduct.setBarcode(barcode);
            supplierProductRepository.save(supplierProduct);
        }
        return Result.success(true);
    }

    public Result<PageResponse<SupplierProduct>> findAllByPage(int pageNum, int pageSize, Long supplierId) {
        SupplierProduct supplierProduct = new SupplierProduct();
        supplierProduct.setSupplierId(supplierId);
        Example<SupplierProduct> example = Example.of(supplierProduct);
        return Result.success(PageResponse.of(supplierProductRepository.findAll(example, PageRequest.of(pageNum,pageSize))));
    }


    public Result<SupplierProduct> findById(Long id) {
        SupplierProduct supplierProduct = supplierProductRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(supplierProduct);
    }
}
