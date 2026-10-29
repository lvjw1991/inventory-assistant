package com.example.recover.service;

import com.example.recover.dto.InventorySaleRow;
import com.example.recover.dto.InventoryStockRow;
import com.example.recover.entity.InventoryStock;
import com.example.recover.entity.SalesMonthlyAvg;
import com.example.recover.exception.BusinessException;
import com.example.recover.repository.SalesRepository;
import com.example.recover.repository.StockRepository;
import com.example.recover.utils.ExcelUtils;
import com.example.recover.vo.*;
import com.example.recover.dto.PurchaseRecommendationProjection;
import com.example.recover.dto.SupplierOptionProjection;
import com.example.recover.repository.PurchaseRecommendationRepository;
import com.example.recover.repository.PurchaseRecommendationSupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRecommendationService {

    private final PurchaseRecommendationRepository productRepository;
    private final PurchaseRecommendationSupplierRepository supplierRepository;
    private final StockRepository stockRepository;
    private final SalesRepository salesRepository;

    @Transactional(readOnly = true)
    public PageResponse<PurchaseRecommendationVO> findRecommendations(
            String name,
            String barcode,
            Long supplierId,
            int pageNum,
            int pageSize) {

        int page = Math.max(pageNum - 1, 0);
        int size = Math.max(1, Math.min(pageSize, 100));

        String normalizedKeyword =
                StringUtils.hasText(name) ? name.trim() : null;

        String normalizedBarcode =
                StringUtils.hasText(barcode) ? barcode.trim() : null;

        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseRecommendationProjection> productPage =
                productRepository.findRecommendations(
                        normalizedKeyword,
                        normalizedBarcode,
                        supplierId,
                        pageable
                );

        List<String> barcodes = productPage.getContent().stream()
                .map(PurchaseRecommendationProjection::getBarcode)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();

        Map<String, List<SupplierOptionVO>> supplierMap;

        if (barcodes.isEmpty()) {
            supplierMap = Collections.emptyMap();
        } else {
            List<SupplierOptionProjection> supplierRows =
                    supplierRepository.findSuppliersByBarcodes(barcodes);

            supplierMap = supplierRows.stream()
                    .collect(Collectors.groupingBy(
                            SupplierOptionProjection::getBarcode,
                            Collectors.mapping(
                                    row -> new SupplierOptionVO(
                                            row.getSupplierName(),
                                            row.getSupplierCode()
                                    ),
                                    Collectors.toList()
                            )
                    ));
        }

        return PageResponse.of(productPage.map(product ->
                new PurchaseRecommendationVO(
                        product.getProductId(),
                        product.getBarcode(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getImgUrl(),
                        product.getPurchasePrice(),
                        product.getStock(),
                        product.getAvgMonthlySalesQty(),
                        calculateStockMonths(
                                product.getStock(),
                                product.getAvgMonthlySalesQty()),
                        supplierMap.getOrDefault(
                                product.getBarcode(),
                                Collections.emptyList()
                        )
                ))
        );
    }

    private BigDecimal calculateStockMonths(BigDecimal stock, BigDecimal avgMonthlySalesQty) {
        // 库存为空时按 0 处理
        BigDecimal actualStock = stock != null
                ? stock
                : BigDecimal.ZERO;

        // 月均销量为空或为 0 时，无法计算库存月数
        if (avgMonthlySalesQty == null
                || avgMonthlySalesQty.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return actualStock.divide(
                avgMonthlySalesQty,
                2,
                RoundingMode.HALF_UP
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<ImportResultVO> importStock(MultipartFile file) throws IOException {
        List<InventoryStockRow> rowList = ExcelUtils.read(file, InventoryStockRow.class);
        return Result.success(updateStock(rowList));
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<ImportResultVO> importSales(MultipartFile file) throws IOException {
        List<InventorySaleRow> rowList = ExcelUtils.read(file, InventorySaleRow.class);
        return Result.success(updateSale(rowList));
    }

    public ImportResultVO updateStock(List<InventoryStockRow> rowList) {
        checkStockData(rowList);
        int success = rowList.size(), skip = 0;
        List<InventoryStock> list = new ArrayList<>();
        for (InventoryStockRow row : rowList) {
            InventoryStock inventoryStock = new InventoryStock();
            inventoryStock.setBarcode(row.getBarcode());
            inventoryStock.setStock(row.getStock());
            list.add(inventoryStock);
        }
        stockRepository.deleteAll();
        stockRepository.saveAll(list);
        return new ImportResultVO(success, skip);
    }

    private void checkStockData(List<InventoryStockRow> rowList) {
        Set<String> set = new HashSet<>();
        Set<String> duplicateCodes = new LinkedHashSet<>();
        List<Integer> emptyBarcodeRows = new ArrayList<>();

        for (int i = 0; i < rowList.size(); i++) {
            InventoryStockRow row = rowList.get(i);
            String code = org.apache.commons.lang3.StringUtils.trimToNull(row.getBarcode());

            if (code == null) {
                emptyBarcodeRows.add(i + 2); // 假设 Excel 第 1 行是表头
                continue;
            }

            if (!set.add(code)) {
                duplicateCodes.add(code);
            }
        }

        List<String> errors = new ArrayList<>();

        if (!emptyBarcodeRows.isEmpty()) {
            errors.add("空 Barcode 所在行：" + emptyBarcodeRows);
        }

        if (!duplicateCodes.isEmpty()) {
            errors.add("重复 Barcode：" + duplicateCodes);
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(500, String.join("；", errors));
        }
    }

    public ImportResultVO updateSale(List<InventorySaleRow> rowList) {
        checkSaleData(rowList);
        int success = rowList.size(), skip = 0;
        List<SalesMonthlyAvg> list = new ArrayList<>();
        for (InventorySaleRow row : rowList) {
            SalesMonthlyAvg salesMonthlyAvg = new SalesMonthlyAvg();
            salesMonthlyAvg.setBarcode(row.getBarcode());
            salesMonthlyAvg.setAvgMonthlySalesQty(row.getAvgMonthlySalesQty());
            list.add(salesMonthlyAvg);
        }
        salesRepository.deleteAll();
        salesRepository.saveAll(list);
        return new ImportResultVO(success, skip);
    }

    private void checkSaleData(List<InventorySaleRow> rowList) {
        Set<String> set = new HashSet<>();
        Set<String> duplicateCodes = new LinkedHashSet<>();
        List<Integer> emptyBarcodeRows = new ArrayList<>();

        for (int i = 0; i < rowList.size(); i++) {
            InventorySaleRow row = rowList.get(i);
            String code = org.apache.commons.lang3.StringUtils.trimToNull(row.getBarcode());

            if (code == null) {
                emptyBarcodeRows.add(i + 2); // 假设 Excel 第 1 行是表头
                continue;
            }

            if (!set.add(code)) {
                duplicateCodes.add(code);
            }
        }

        List<String> errors = new ArrayList<>();

        if (!emptyBarcodeRows.isEmpty()) {
            errors.add("空 Barcode 所在行：" + emptyBarcodeRows);
        }

        if (!duplicateCodes.isEmpty()) {
            errors.add("重复 Barcode：" + duplicateCodes);
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(500, String.join("；", errors));
        }
    }
}
