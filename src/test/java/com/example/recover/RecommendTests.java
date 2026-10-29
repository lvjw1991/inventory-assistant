package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.example.recover.dto.*;
import com.example.recover.service.PurchaseRecommendationService;
import com.example.recover.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
class RecommendTests {

    @Autowired
    private PurchaseRecommendationService service;


    @Test
    void testQuery(){
        PageResponse<PurchaseRecommendationVO> recommendations = service.findRecommendations(null, null,
                13L, 0, 20);
        System.out.println(JSON.toJSONString(recommendations));
    }

    // 从本地excel读取行数据
    private <T> List<T> readExcel(String fileName, Class<T> clazz) {
        String path = "src/test/resources/" + fileName;
        return EasyExcel.read(path)
                .head(clazz)
                .sheet()
                .doReadSync();
    }

    @Test
    void importStock() {
        List<InventoryStockRow> rowList = readExcel("stock.xls", InventoryStockRow.class);
        ImportResultVO importResultVO = service.updateStock(rowList);
        System.out.println("success" + importResultVO.getSuccess());
        System.out.println("skip" + importResultVO.getSkip());
    }

    @Test
    void importSale() {
        List<InventorySaleRow> rowList = readExcel("sale.xls", InventorySaleRow.class);
        ImportResultVO importResultVO = service.updateSale(rowList);
        System.out.println("success" + importResultVO.getSuccess());
        System.out.println("skip" + importResultVO.getSkip());
    }


}
