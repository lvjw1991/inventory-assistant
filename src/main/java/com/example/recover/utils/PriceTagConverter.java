package com.example.recover.utils;

import com.example.recover.entity.Product;
import com.example.recover.vo.PriceTagVO;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
public class PriceTagConverter {

    public PriceTagVO toVO(Product entity){
        PriceTagVO vo = new PriceTagVO();
        vo.setName(entity.getName());
        vo.setNameFi(entity.getNameFi());
        vo.setBarcode(entity.getBarcode());
        vo.setSalePrice(entity.getSalePrice());
        vo.setImgUrl(entity.getImgUrl());
        vo.setNetContent(entity.getNetContent());
        vo.setNetUnit(entity.getNetUnit());
        if(entity.getSalePrice()!=null && entity.getNetContent()!=null && entity.getNetUnit()!=null){
            vo.setUnitPrice(entity.getSalePrice().divide(entity.getNetContent(), 2, RoundingMode.HALF_UP));
        }
        return vo;
    }
}
