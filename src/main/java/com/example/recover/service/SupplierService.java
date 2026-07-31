package com.example.recover.service;

import com.example.recover.dto.PageResponse;
import com.example.recover.dto.Result;
import com.example.recover.dto.SupplierRequest;
import com.example.recover.entity.Supplier;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;


    public Result<PageResponse<Supplier>> findAllByPage(int pageNum, int pageSize) {
        return Result.success(PageResponse.of(supplierRepository.findAll(PageRequest.of(pageNum,pageSize))));
    }

    public Result<Supplier> findById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(supplier);
    }

    @Transactional
    public Result<Supplier> create(SupplierRequest supplierRequest) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierRequest.getSupplierName());
        supplier.setStatus(true);
        return Result.success(supplierRepository.save(supplier));
    }

    @Transactional
    public Result<Supplier> update(SupplierRequest supplierRequest) {
        Supplier supplier = findById(supplierRequest.getId()).getData();
        supplier.setSupplierName(supplierRequest.getSupplierName());
        return Result.success(supplierRepository.save(supplier));
    }


    public Result<Boolean> delete(Long id) {
        Supplier supplier = findById(id).getData();
        supplierRepository.delete(supplier);
        return Result.success(true);
    }
}
