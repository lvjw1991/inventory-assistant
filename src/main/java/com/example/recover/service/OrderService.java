package com.example.recover.service;

import com.example.recover.dto.OrderItemRow;
import com.example.recover.dto.OrderQuery;
import com.example.recover.dto.OrderRequest;
import com.example.recover.entity.*;
import com.example.recover.exception.BusinessException;
import com.example.recover.repository.*;
import com.example.recover.utils.*;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.ReceivingOrderVO;
import com.example.recover.vo.Result;
import com.example.recover.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ReceivingOrderRepository orderRepository;

    private final ReceivingOrderItemRepository orderItemRepository;

    private final SupplierProductService supplierProductService;

    private final ExpiryRecordService expiryRecordService;

    private final OrderConverter orderConverter;

    private final SupplierRepository supplierRepository;

    private final SupplierProductRepository supplierProductRepository;

    private final ProductRepository productRepository;


    public Result<PageResponse<ReceivingOrderVO>> findAllByPage(OrderQuery orderQuery) {
        Pageable pageable = PageRequest.of(
                orderQuery.getPageNum(),
                orderQuery.getPageSize(),
                Sort.by(Sort.Direction.DESC, "receiveDate")
        );
        return Result.success(PageResponse.of(orderRepository.findPage(orderQuery.getSupplierId(),
                orderQuery.getStartDate(), orderQuery.getEndDate(), pageable)));
    }

    public Result<ReceivingOrderVO> findById(Long id) {
        ReceivingOrder order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        ReceivingOrderVO vo = orderConverter.toVO(order);
        Supplier supplier = supplierRepository.findById(order.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        vo.setSupplierName(supplier.getSupplierName());
        return Result.success(vo);
    }

    public ReceivingOrder findEntityById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
    }

    @Transactional
    public Result<ReceivingOrderVO> create(OrderRequest request) {
        ReceivingOrder order = new ReceivingOrder();
        order.setSupplierId(request.getSupplierId());
        order.setNumber(request.getNumber());
        order.setReceiveDate(request.getReceiveDate());
        order.setProgress(OrderProcess.DRAFT);
        order.setTemperature(request.getTemperature());
        order.setTransport(request.getTransport());
        return Result.success(orderConverter.toVO(orderRepository.save(order)));
    }

    @Transactional
    public Result<ReceivingOrderVO> update(OrderRequest request) {
        ReceivingOrder order = findEntityById(request.getId());
        order.setSupplierId(request.getSupplierId());
        order.setNumber(request.getNumber());
        order.setReceiveDate(request.getReceiveDate());
        order.setTemperature(request.getTemperature());
        order.setTransport(request.getTransport());
        return Result.success(orderConverter.toVO(orderRepository.save(order)));
    }


    @Transactional
    public Result<Boolean> delete(Long id) {
        ReceivingOrder order = findEntityById(id);
        orderRepository.delete(order);
        orderItemRepository.deleteByOrderId(order.getId());
        return Result.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<ImportResultVO> importExcel(MultipartFile file, Long orderId) throws IOException {
        ReceivingOrder order = findEntityById(orderId);
        if (order.getProgress().equals(OrderProcess.COMPLETED)) {
            return Result.fail(500, "已完成点货");
        }
        List<OrderItemRow> rows = ExcelUtils.read(file, OrderItemRow.class);
        ImportResultVO importResultVO = importOrderItems(rows, orderId, order.getSupplierId());
        updateProcess(order);
        return Result.success(importResultVO);
    }

    public ReceivingOrder updateProcess(ReceivingOrder order) {
        order.setProgress(OrderProcess.READY);
        return orderRepository.save(order);
    }

    public ImportResultVO importOrderItems(List<OrderItemRow> rows, Long orderId, Long supplierId) {
        int success = 0, skip = 0;
        checkData(rows);
        List<String> excelBarcodeList = rows.stream().map(OrderItemRow::getBarcode).distinct().toList();
        //查询 Excel 中已有 Barcode 对应的商品
        Map<String, Product> barCodeProductMap = getBarCodeData(excelBarcodeList);
        //查询供应商映射表，再查询Barcode对应的商品
        Map<String, Product> supplierCodeProductMap = getSupplierCodeData(supplierId, excelBarcodeList);
        List<ReceivingOrderItem> orderItemList = new ArrayList<>();
        for (OrderItemRow row : rows) {
            ReceivingOrderItem orderItem = new ReceivingOrderItem();
            orderItem.setReceivingOrderId(orderId);
            orderItem.setSupplierCode(row.getSupplierCode());
            orderItem.setProductName(row.getProductName());
            orderItem.setOrderQty(row.getOrderQty());
            orderItem.setTotal(row.getTotal());
            orderItem.setUnitPrice(row.getUnitPrice());
            orderItem.setCategory(row.getCategory());
            orderItem.setCheckStatus(CheckStatus.UNCHECKED);
            orderItem.setCartonQty(row.getCartonQty());
            orderItem.setOtherBoxQty(row.getOtherBoxQty());
            orderItem.setRemark(row.getRemark());
            String barcode = StringUtils.trimToNull(row.getBarcode());
            if (barcode != null) {
                // 优先使用 Excel 中的 Barcode
                orderItem.setBarcode(barcode);
                Product product = barCodeProductMap.get(barcode);
                if (product != null) {
                    orderItem.setPosPurchasePrice(product.getPurchasePrice());
                }
            } else {
                // Excel Barcode 为空时，使用供应商编码映射
                Product product = supplierCodeProductMap.get(row.getSupplierCode());
                if (product != null) {
                    orderItem.setBarcode(product.getBarcode());
                    orderItem.setPosPurchasePrice(product.getPurchasePrice());
                }
            }
            orderItemList.add(orderItem);
        }
        orderItemRepository.saveAll(orderItemList);
        success = orderItemList.size();
        return new ImportResultVO(success, skip);
    }

    /**
     * 1. 根据barcode更新进价
     * 2. 保存 supplier_product（不存在则新增）
     * 3. 保存 expiry_record（不存在则新增）
     *
     * @param id
     * @return
     */
    public Result<Boolean> complete(Long id) {
        ReceivingOrder order = findEntityById(id);
        if (order.getProgress().equals(OrderProcess.READY)) {
            return Result.fail(500, "请先点货");
        }
        if (order.getProgress().equals(OrderProcess.COMPLETED)) {
            return Result.fail(500, "已完成点货");
        }
        List<ReceivingOrderItem> orderItemList = orderItemRepository.findByReceivingOrderId(id);
        if (orderItemList.isEmpty()) {
            return Result.fail(500, "请先导入货单");
        }
        List<CheckStatus> list = orderItemList.stream().map(ReceivingOrderItem::getCheckStatus).distinct().toList();
        if (list.contains(CheckStatus.UNCHECKED)) {
            return Result.fail(500, "请先点货");
        }
        updatePrice(orderItemList);
        supplierProductService.saveAll(orderItemList, order.getSupplierId());
        expiryRecordService.saveAll(orderItemList);
        order.setProgress(OrderProcess.COMPLETED);
        orderRepository.save(order);
        return Result.success(true);
    }

    public void updateProcess(Long receivingOrderId) {
        ReceivingOrder order = findEntityById(receivingOrderId);
        if (order.getProgress().equals(OrderProcess.READY)) {
            order.setProgress(OrderProcess.CHECKING);
            orderRepository.save(order);
        }
    }

    private void checkData(List<OrderItemRow> rowList) {
        Set<String> set = new HashSet<>();
        Set<String> duplicateCodes = new LinkedHashSet<>();
        List<Integer> emptyBarcodeRows = new ArrayList<>();

        for (int i = 0; i < rowList.size(); i++) {
            OrderItemRow row = rowList.get(i);
            String code = StringUtils.trimToNull(row.getSupplierCode());

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
            errors.add("空 货号 所在行：" + emptyBarcodeRows);
        }

        if (!duplicateCodes.isEmpty()) {
            errors.add("重复 货号：" + duplicateCodes);
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(500, String.join("；", errors));
        }
    }

    private Map<String, Product> getBarCodeData(List<String> barcodeList) {
        if (barcodeList == null || barcodeList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Product> byBarcodeIn = productRepository.findByBarcodeIn(barcodeList);
        // Barcode -> Product
        return  byBarcodeIn.stream()
                .filter(p -> p.getBarcode() != null)
                .collect(Collectors.toMap(
                        Product::getBarcode,
                        Function.identity(),
                        (a, b) -> a
                ));
    }

    private Map<String, Product> getSupplierCodeData(Long supplierId, List<String> excelBarcodeList) {
        List<SupplierProduct> bySupplierId = supplierProductRepository.findBySupplierId(supplierId);
        if (bySupplierId.isEmpty()) {
            return Collections.emptyMap();
        }
        // Excel 已有的 Barcode
        Set<String> excelBarcodes = excelBarcodeList == null
                ? Collections.emptySet()
                : excelBarcodeList.stream()
                  .filter(StringUtils::isNotBlank)
                  .map(String::trim)
                  .collect(Collectors.toSet());
        // 提取供应商映射中的 Barcode，排除 Excel 已有的
        List<String> barcodeList = bySupplierId.stream()
                .map(SupplierProduct::getBarcode)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .filter(barcode -> !excelBarcodes.contains(barcode))
                .distinct()
                .toList();
        Map<String, Product> productMap = getBarCodeData(barcodeList);
        // supplierCode -> Product
        Map<String, Product> supplierCodeProductMap = new HashMap<>();
        for (SupplierProduct sp : bySupplierId) {
            if (sp.getSupplierCode() == null || sp.getBarcode() == null) {
                continue;
            }

            Product product = productMap.get(sp.getBarcode());
            if (product == null) {
                continue;
            }

            // 一个货号对应多个 Barcode 时，不能直接覆盖
            supplierCodeProductMap.putIfAbsent(
                    sp.getSupplierCode(),
                    product
            );
        }
        return supplierCodeProductMap;
    }

    private void updatePrice(List<ReceivingOrderItem> orderItemList) {
        List<String> barcodeList = orderItemList.stream().map(ReceivingOrderItem::getBarcode).distinct().toList();
        Map<String, Product> barCodeData = getBarCodeData(barcodeList);
        List<ReceivingOrderItem> updateList = new ArrayList<>();
        for (ReceivingOrderItem item : orderItemList) {
            String barcode = StringUtils.trimToNull(item.getBarcode());
            if (barcode == null) {
                continue;
            }
            Product product = barCodeData.get(barcode);
            if (product == null || product.getPurchasePrice() == null) {
                continue;
            }
            if (item.getPosPurchasePrice() == null
                    || item.getPosPurchasePrice().compareTo(product.getPurchasePrice()) != 0) {
                item.setPosPurchasePrice(product.getPurchasePrice());
                updateList.add(item);
            }
        }
        if (!updateList.isEmpty()) {
            orderItemRepository.saveAll(updateList);
        }
    }
}
