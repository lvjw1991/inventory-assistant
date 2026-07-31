package com.example.recover.service;

import com.example.recover.dto.Result;
import com.example.recover.dto.SupplierProductRequest;
import com.example.recover.entity.SupplierProduct;
import com.example.recover.repository.SupplierProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierProductService {

    private final SupplierProductRepository supplierProductRepository;

    /**
     * 录入时同时维护供应商code和barcode关系
     * @param
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


}
