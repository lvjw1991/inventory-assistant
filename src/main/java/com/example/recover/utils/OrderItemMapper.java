package com.example.recover.utils;

import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.vo.OrderItemVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    OrderItemVO toVo(ReceivingOrderItem item);
    List<OrderItemVO> toVOList(List<ReceivingOrderItem> itemList);
}
