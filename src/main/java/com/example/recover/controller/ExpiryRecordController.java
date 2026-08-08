package com.example.recover.controller;

import com.example.recover.dto.ExpiryConfirmRequest;
import com.example.recover.dto.ExpiryProcessRequest;
import com.example.recover.dto.ExpiryRecordRequest;
import com.example.recover.dto.RecordQuery;
import com.example.recover.service.ExpiryRecordService;
import com.example.recover.vo.ExpiryRecordVO;
import com.example.recover.vo.ImportResultVO;
import com.example.recover.vo.PageResponse;
import com.example.recover.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class ExpiryRecordController {

    private final ExpiryRecordService expiryRecordService;

    /**
     * 查询全部
     */
    @GetMapping
    public Result<PageResponse<ExpiryRecordVO>> search(RecordQuery query) {
        return expiryRecordService.search(query);
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<ExpiryRecordVO> getById(@PathVariable Long id) {
        return expiryRecordService.findById(id);
    }

    /**
     * 创建
     */
    @PostMapping
    public Result<ExpiryRecordVO> create(@Valid @RequestBody ExpiryRecordRequest request) {
        return expiryRecordService.create(request);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<ExpiryRecordVO> update(@PathVariable Long id, @Valid @RequestBody ExpiryRecordRequest request) {
        request.setId(id);
        return expiryRecordService.update(request);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return expiryRecordService.delete(id);
    }

    /**
     * 导入库存
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return expiryRecordService.importExcel(file);
    }

    /**
     * 确认是否存在
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Long id, @Valid @RequestBody ExpiryConfirmRequest request) {
        request.setId(id);
        return expiryRecordService.confirm(request);
    }

    /**
     * 处理是否打折
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/{id}/process")
    public Result<Boolean> process(@PathVariable Long id, @Valid @RequestBody ExpiryProcessRequest request) {
        request.setId(id);
        return expiryRecordService.process(request);
    }


}
