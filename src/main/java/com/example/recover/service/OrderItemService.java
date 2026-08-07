package com.example.recover.service;

import com.example.recover.dto.OrderItemCheckRequest;
import com.example.recover.dto.OrderItemRequest;
import com.example.recover.dto.SupplierProductRequest;
import com.example.recover.entity.ReceivingOrder;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ReceivingOrderItemRepository;
import com.example.recover.utils.OrderItemMapper;
import com.example.recover.vo.OrderItemVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final ReceivingOrderItemRepository orderItemRepository;

    private final SupplierProductService supplierProductService;

    private final OrderService orderService;

    private final ExpiryRecordService expiryRecordService;

    private final OrderItemMapper orderItemMapper;

    public Result<PageResponse<ReceivingOrderItem>> findAllByPage(int pageNum, int pageSize, Long orderId) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(orderId);
        Example<ReceivingOrderItem> example = Example.of(orderItem);
        return Result.success(PageResponse.of(orderItemRepository.findAll(example, PageRequest.of(pageNum, pageSize))));
    }

    public Result<ReceivingOrderItem> findById(Long id) {
        ReceivingOrderItem orderItem = orderItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return Result.success(orderItem);
    }

    @Transactional
    public Result<ReceivingOrderItem> create(OrderItemRequest request) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(request.getReceivingOrderId());
        orderItem.setSupplierCode(request.getSupplierCode());
        orderItem.setProductName(request.getProductName());
        orderItem.setBarcode(request.getBarcode());
        orderItem.setOrderQty(request.getOrderQty());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setTotal(request.getTotal());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setCategory(request.getCategory());
        orderItem.setSugar(request.getSugar());
        return Result.success(orderItemRepository.save(orderItem));
    }

    private List<String> deduplicationList(List<String> expiryDate) {
        if (expiryDate == null || expiryDate.size() < 2) {
            return expiryDate;
        }
        return expiryDate.stream().distinct().sorted().toList();
    }

    @Transactional
    public Result<ReceivingOrderItem> update(OrderItemRequest request) {
        ReceivingOrderItem orderItem = findById(request.getId()).getData();
        orderItem.setSupplierCode(request.getSupplierCode());
        orderItem.setProductName(request.getProductName());
        orderItem.setBarcode(request.getBarcode());
        orderItem.setOrderQty(request.getOrderQty());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setTotal(request.getTotal());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setCategory(request.getCategory());
        orderItem.setSugar(request.getSugar());
        return Result.success(orderItemRepository.save(orderItem));
    }

    @Transactional
    public Result<Boolean> delete(Long id) {
        ReceivingOrderItem item = findById(id).getData();
        orderItemRepository.delete(item);
        return Result.success(true);
    }

    /**
     *
     * 1. 更新 receiving_order_item
     * 2. 保存 supplier_product（不存在则新增）
     * 3. 保存 expiry_record（不存在则新增）
     *
     * @param id
     * @param request
     * @return
     */
    @Transactional
    public Result<Boolean> check(Long id, OrderItemCheckRequest request) {
        ReceivingOrderItem orderItem = updateItem(id, request);
        ReceivingOrder order = orderService.findById(orderItem.getReceivingOrderId()).getData();
        Long supplierId = order.getSupplierId();
        SupplierProductRequest supplierProductRequest = new SupplierProductRequest();
        supplierProductRequest.setBarcode(orderItem.getBarcode());
        supplierProductRequest.setSupplierCode(orderItem.getSupplierCode());
        supplierProductRequest.setSupplierId(supplierId);
        supplierProductService.save(supplierProductRequest);
        expiryRecordService.save(orderItem);
        return Result.success(true);
    }

    public ReceivingOrderItem updateItem(Long id, OrderItemCheckRequest request) {
        ReceivingOrderItem orderItem = findById(id).getData();
        orderItem.setBarcode(request.getBarcode());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setSugar(request.getSugar());
        orderItem.setStatus(request.getStatus());
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItemVO> findAllByOrderId(Long orderId) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(orderId);
        Example<ReceivingOrderItem> example = Example.of(orderItem);
        List<ReceivingOrderItem> list = orderItemRepository.findAll(example);
        return orderItemMapper.toVOList(list);
    }
}
