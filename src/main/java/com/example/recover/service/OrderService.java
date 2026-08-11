package com.example.recover.service;

import com.example.recover.dto.OrderItemRow;
import com.example.recover.dto.OrderQuery;
import com.example.recover.dto.OrderRequest;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.entity.Supplier;
import com.example.recover.repository.ReceivingOrderItemRepository;
import com.example.recover.repository.SupplierRepository;
import com.example.recover.utils.*;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.ReceivingOrderVO;
import com.example.recover.vo.Result;
import com.example.recover.entity.ReceivingOrder;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ReceivingOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
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

    @Transactional
    public Result<ImportResultVO> importExcel(MultipartFile file, Long orderId) throws IOException {
        ReceivingOrder order = findEntityById(orderId);
        List<OrderItemRow> rows = ExcelUtils.read(file, OrderItemRow.class);
        ImportResultVO importResultVO = importOrderItems(rows, orderId);
        updateProcess(order);
        return Result.success(importResultVO);
    }

    public ReceivingOrder updateProcess(ReceivingOrder order) {
        order.setProgress(OrderProcess.READY);
        return orderRepository.save(order);
    }

    public ImportResultVO importOrderItems(List<OrderItemRow> rows, Long orderId) {
        int success = 0, skip = 0;
        List<ReceivingOrderItem> orderItemList = new ArrayList<>();
        for (OrderItemRow row : rows) {
            ReceivingOrderItem orderItem = new ReceivingOrderItem();
            orderItem.setReceivingOrderId(orderId);
            orderItem.setSupplierCode(row.getSupplierCode());
            orderItem.setProductName(row.getProductName());
            orderItem.setOrderQty(row.getOrderQty());
            orderItem.setTotal(row.getTotal());
            orderItem.setCategory(row.getCategory());
            orderItem.setCheckStatus(CheckStatus.UNCHECKED);
            orderItemList.add(orderItem);
        }
        List<ReceivingOrderItem> uniqueList = orderItemList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ReceivingOrderItem::getSupplierCode))),
                        ArrayList::new
                ));
        orderItemRepository.saveAll(uniqueList);
        success = uniqueList.size();
        skip = orderItemList.size() - uniqueList.size();
        return new ImportResultVO(success, skip);
    }

    /**
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
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(id);
        Example<ReceivingOrderItem> example = Example.of(orderItem);
        List<ReceivingOrderItem> orderItemList = orderItemRepository.findAll(example);
        if (orderItemList.isEmpty()) {
            return Result.fail(500, "请先导入货单");
        }
        List<CheckStatus> list = orderItemList.stream().map(ReceivingOrderItem::getCheckStatus).distinct().toList();
        if (list.contains(CheckStatus.UNCHECKED)) {
            return Result.fail(500, "请先点货");
        }
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
}
