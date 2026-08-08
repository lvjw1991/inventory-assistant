package com.example.recover.service;

import com.example.recover.entity.ReceivingOrderItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 批量维护
     * @param orderItemList
     * @param supplierId
     */
    @Transactional
    public void saveAll(List<ReceivingOrderItem> orderItemList, Long supplierId) {
        List<String> supplierCodeList = orderItemList.stream().map(ReceivingOrderItem::getSupplierCode).distinct().toList();
        List<SupplierProduct> supplierProductList = supplierProductRepository.findBySupplierIdAndSupplierCodeIn(supplierId, supplierCodeList);
        Set<String> existingKeys = supplierProductList.stream()
                .map(p -> p.getSupplierCode() + ":" + p.getBarcode())
                .collect(Collectors.toSet());
        List<SupplierProduct> saveList = new ArrayList<>();
        for (ReceivingOrderItem item : orderItemList) {
            if (item.getSupplierCode() == null
                    || item.getBarcode() == null) {
                continue;
            }
            String key = item.getSupplierCode() + ":" + item.getBarcode();
            // 数据库已有，跳过
            if (existingKeys.contains(key)) {
                continue;
            }
            SupplierProduct supplierProduct = new SupplierProduct();
            supplierProduct.setSupplierId(supplierId);
            supplierProduct.setSupplierCode(item.getSupplierCode());
            supplierProduct.setBarcode(item.getBarcode());
            saveList.add(supplierProduct);
            // 加入 Set，防止本次 Excel 自己重复
            existingKeys.add(key);
        }
        if (!saveList.isEmpty()) {
            supplierProductRepository.saveAll(saveList);
        }
    }

}
