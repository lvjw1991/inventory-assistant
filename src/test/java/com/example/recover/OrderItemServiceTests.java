package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.example.recover.dto.OrderItemCheckRequest;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.service.OrderItemService;
import com.example.recover.utils.CheckStatus;
import com.example.recover.vo.OrderItemVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderItemServiceTests {

    @Autowired
    private OrderItemService service;

    @Test
    void page() {
        Long orderId=5L;
        Result<PageResponse<ReceivingOrderItem>> allByPage = service.findAllByPage(0, 10, orderId, CheckStatus.UNCHECKED);
        System.out.println(JSON.toJSONString(allByPage));
        assertEquals("success", allByPage.getMessage());

    }

    @Test
    void check() {
        Long orderItemId=95L;
        OrderItemCheckRequest request = new OrderItemCheckRequest();
        request.setStatus(CheckStatus.PASS);
        request.setBarcode("8082481440560");
        request.setExpiryDate(List.of("2026-09-05","2026-09-08"));
        Result<Boolean> check = service.check(orderItemId, request);
        assertEquals("success", check.getMessage());

    }

    @Test
    void export(){
        Long orderId=5L;
        List<OrderItemVO> allByOrderId = service.findAllByOrderId(orderId);
        // 写入本地文件
        String path = "src/test/resources/export_" +
                LocalDate.now() + ".xlsx";
        EasyExcel.write(path, OrderItemVO.class)
                .sheet("test")
                .doWrite(allByOrderId);
    }

    @Test
    void checkAll(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Long orderId=5L;
        Result<PageResponse<ReceivingOrderItem>> allByPage = service.findAllByPage(0, 50, orderId, null);
        List<ReceivingOrderItem> list = allByPage.getData().getList();
        for (ReceivingOrderItem item : list){
            if(item.getCheckStatus().equals(CheckStatus.UNCHECKED)) {
                // 简化的任意日期生成示例
                int year = ThreadLocalRandom.current().nextInt(2027, 2031);
                int month = ThreadLocalRandom.current().nextInt(1, 13);
                // 动态获取月份天数，避免非法日期
                int day = ThreadLocalRandom.current().nextInt(1, LocalDate.of(year, month, 1).lengthOfMonth() + 1);
                OrderItemCheckRequest request = new OrderItemCheckRequest();
                request.setStatus(CheckStatus.PASS);
                request.setBarcode(UUID.randomUUID().toString());
                request.setExpiryDate(List.of(LocalDate.of(year, month, day).format(formatter)));
                service.check(item.getId(), request);
            }
        }

    }

    public static void main(String[] args) {
        String path = "src/test/resources/test0807.xlsx";

        List<OrderItemVO> list = new ArrayList<>();

        OrderItemVO vo = new OrderItemVO();
        vo.setProductName("测试商品");
        vo.setSupplierCode("3005");
        vo.setOrderQty(10);

        list.add(vo);

        EasyExcel.write(path, OrderItemVO.class)
                .sheet("test")
                .doWrite(list);
    }

}
