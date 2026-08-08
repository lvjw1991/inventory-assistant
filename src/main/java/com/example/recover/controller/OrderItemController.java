package com.example.recover.controller;

import com.alibaba.excel.EasyExcel;
import com.example.recover.dto.OrderItemCheckRequest;
import com.example.recover.dto.OrderItemRequest;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.service.OrderItemService;
import com.example.recover.utils.CheckStatus;
import com.example.recover.vo.OrderItemVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<ReceivingOrderItem>> search(
            @RequestParam("orderId") Long orderId,
            @RequestParam(required = false) CheckStatus status,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return orderItemService.findAllByPage(pageNum, pageSize, orderId, status);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<ReceivingOrderItem> getById(@PathVariable Long id) {
        return orderItemService.findById(id);
    }

    /**
     * 创建
     */
    @PostMapping
    public Result<ReceivingOrderItem> create(@Valid @RequestBody OrderItemRequest request) {
        return orderItemService.create(request);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<ReceivingOrderItem> update(@PathVariable Long id, @Valid @RequestBody OrderItemRequest request) {
        request.setId(id);
        return orderItemService.update(request);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return orderItemService.delete(id);
    }

    /**
     * 点货
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/{id}/check")
    public Result<Boolean> check(@PathVariable Long id, @Valid @RequestBody OrderItemCheckRequest request) {
        return orderItemService.check(id, request);
    }

    @GetMapping("/export/{orderId}")
    public void export(@PathVariable Long orderId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode("货单列表.xlsx", StandardCharsets.UTF_8));

        List<OrderItemVO> list = orderItemService.findAllByOrderId(orderId);

        EasyExcel.write(response.getOutputStream(), OrderItemVO.class)
                .sheet("货单列表")
                .doWrite(list);
    }

}
