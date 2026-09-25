package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemListVO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemListConverter {

    public OrderItemListVO toVo(ReceivingOrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemListVO listVO = new OrderItemListVO();
        listVO.setId(item.getId());
        listVO.setSupplierCode(item.getSupplierCode());
        listVO.setProductName(item.getProductName());
        listVO.setBarcode(item.getBarcode());
        listVO.setOrderQty(item.getOrderQty());
        listVO.setTotal(item.getTotal());
        listVO.setExpiryDate(item.getExpiryDate());
        listVO.setCheckStatus(item.getCheckStatus() != null ? item.getCheckStatus().name() : "");
        return listVO;
    }
}
