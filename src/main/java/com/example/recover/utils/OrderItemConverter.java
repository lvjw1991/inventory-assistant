package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemVO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {

    public OrderItemVO toVo(ReceivingOrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setId(item.getId());
        orderItemVO.setSupplierCode(item.getSupplierCode());
        orderItemVO.setProductName(item.getProductName());
        orderItemVO.setBarcode(item.getBarcode());
        orderItemVO.setOrderQty(item.getOrderQty());
        orderItemVO.setActualQty(item.getActualQty());
        orderItemVO.setTotal(item.getTotal());
        orderItemVO.setExpiryDate(item.getExpiryDate());
        orderItemVO.setUnitPrice(item.getUnitPrice());
        orderItemVO.setCategory(item.getCategory());
        orderItemVO.setSugar(item.getSugar());
        orderItemVO.setCheckStatus(item.getCheckStatus() != null ? item.getCheckStatus().name() : "");
        orderItemVO.setDamageQty(item.getDamageQty());
        orderItemVO.setRemark(item.getRemark());
        orderItemVO.setCartonQty(item.getCartonQty());
        return orderItemVO;
    }
}
