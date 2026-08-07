package com.example.recover.controller;

import com.example.recover.dto.ExpiryConfirmRequest;
import com.example.recover.dto.ExpiryProcessRequest;
import com.example.recover.dto.RecordQuery;
import com.example.recover.service.ExpiryRecordService;
import com.example.recover.vo.*;
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
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return expiryRecordService.delete(id);
    }


    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return expiryRecordService.importExcel(file);
    }

    @PostMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Long id, @Valid @RequestBody ExpiryConfirmRequest request) {
        request.setId(id);
        return expiryRecordService.confirm(request);
    }

    @PostMapping("/{id}/process")
    public Result<Boolean> process(@PathVariable Long id, @Valid @RequestBody ExpiryProcessRequest request) {
        request.setId(id);
        return expiryRecordService.process(request);
    }


}
