package com.example.recover;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.example.recover.dto.OrderItemRow;
import com.example.recover.dto.OrderQuery;
import com.example.recover.dto.OrderRequest;
import com.example.recover.entity.ReceivingOrder;
import com.example.recover.service.OrderService;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.ReceivingOrderVO;
import com.example.recover.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    // 从本地excel读取行数据
    private List<OrderItemRow> readExcel(String fileName) {
        String path = "src/test/resources/" + fileName;
        return EasyExcel.read(path)
                .head(OrderItemRow.class)
                .sheet()
                .doReadSync();
    }

    @Test
    void mainProcess() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setReceiveDate(LocalDate.now());
        orderRequest.setNumber(1);
        orderRequest.setSupplierId(4L);
        orderRequest.setTransport("transport");
        orderRequest.setTemperature("temperature");
        Result<ReceivingOrderVO> receivingOrderResult = orderService.create(orderRequest);
        assertEquals("success", receivingOrderResult.getMessage());
        ReceivingOrderVO data = receivingOrderResult.getData();
        Long orderId = data.getId();
        List<OrderItemRow> rows = readExcel("test.xlsx");

        ImportResultVO result = orderService.importOrderItems(rows, orderId);
        ReceivingOrder order = orderService.updateProcess(orderService.findEntityById(orderId));

        assertEquals(47, result.getSuccess());
        assertEquals(1, result.getSkip());
        assertEquals("READY", order.getProgress().name());
    }

    @Test
    void complete(){
        Result<Boolean> complete = orderService.complete(5L);
        assertEquals("success", complete.getMessage());
    }


    @Test
    void page() {
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.setSupplierId(4L);
        Result<PageResponse<ReceivingOrderVO>> allByPage = orderService.findAllByPage(orderQuery);
        System.out.println(JSON.toJSONString(allByPage));
        assertEquals("success", allByPage.getMessage());

    }

}
