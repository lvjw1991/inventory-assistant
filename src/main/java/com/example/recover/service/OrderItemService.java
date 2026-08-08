package com.example.recover.service;

import com.example.recover.dto.OrderItemCheckRequest;
import com.example.recover.dto.OrderItemRequest;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ReceivingOrderItemRepository;
import com.example.recover.utils.CheckStatus;
import com.example.recover.utils.OrderItemConverter;
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

    private final OrderItemConverter orderItemConverter;

    private final OrderService orderService;

    public Result<PageResponse<ReceivingOrderItem>> findAllByPage(int pageNum, int pageSize, Long orderId, CheckStatus checkStatus) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(orderId);
        orderItem.setCheckStatus(checkStatus);
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
        orderItem.setCheckStatus(request.getCheckStatus());
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
        orderItem.setCheckStatus(request.getCheckStatus());
        return Result.success(orderItemRepository.save(orderItem));
    }

    @Transactional
    public Result<Boolean> delete(Long id) {
        ReceivingOrderItem item = findById(id).getData();
        orderItemRepository.delete(item);
        return Result.success(true);
    }

    /**
     * 0. 更新order状态
     * 1. 更新 receiving_order_item
     *
     *
     * @param id
     * @param request
     * @return
     */
    @Transactional
    public Result<Boolean> check(Long id, OrderItemCheckRequest request) {
        ReceivingOrderItem orderItem = findById(id).getData();
        orderItem.setBarcode(request.getBarcode());
        orderItem.setActualQty(request.getActualQty());
        orderItem.setExpiryDate(Strings.join(deduplicationList(request.getExpiryDate()), ','));
        orderItem.setSugar(request.getSugar());
        orderItem.setCheckStatus(request.getStatus());
        orderItemRepository.save(orderItem);
        orderService.updateProcess(orderItem.getReceivingOrderId());
        return Result.success(true);
    }

    public List<OrderItemVO> findAllByOrderId(Long orderId) {
        ReceivingOrderItem orderItem = new ReceivingOrderItem();
        orderItem.setReceivingOrderId(orderId);
        Example<ReceivingOrderItem> example = Example.of(orderItem);
        return orderItemRepository.findAll(example).stream().map(orderItemConverter::toVo).toList();
    }
}
