package com.example.recover.service;

import com.example.recover.dto.BarcodeStockRow;
import com.example.recover.dto.ExpiryConfirmRequest;
import com.example.recover.dto.ExpiryProcessRequest;
import com.example.recover.dto.RecordQuery;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ExpiryRecordRepository;
import com.example.recover.repository.RecordSpec;
import com.example.recover.utils.ExcelUtils;
import com.example.recover.utils.ExpiryRecordConverter;
import com.example.recover.vo.*;
import com.example.recover.entity.ExpiryRecord;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpiryRecordService {

    private final ExpiryRecordRepository expiryRecordRepository;

    private final ExpiryRecordConverter expiryRecordConverter;

    public Result<PageResponse<ExpiryRecordVO>> search(RecordQuery query) {
        Pageable pageable = PageRequest.of(
                query.getPage() - 1, query.getSize(),
                Sort.by("expiryDate").ascending());

        return Result.success(PageResponse.of(expiryRecordRepository
                .findAll(RecordSpec.build(query), pageable).map(expiryRecordConverter::toVO)));
    }

    @Transactional
    public void save(ReceivingOrderItem orderItem) {
        String barcode = orderItem.getBarcode();
        String expiryDate = orderItem.getExpiryDate();
        String[] dateArray = expiryDate.split(",");
        for (String dateString : dateArray) {
            LocalDate date = LocalDate.parse(dateString);
            boolean exist = expiryRecordRepository.existsByBarcodeAndExpiryDate(barcode, date);
            if (!exist) {
                ExpiryRecord expiryRecord = new ExpiryRecord();
                expiryRecord.setBarcode(barcode);
                expiryRecord.setExpiryDate(date);
                expiryRecord.setCategory(orderItem.getCategory());
                expiryRecord.setConfirmStatus(false);
                expiryRecord.setProcessStatus(false);
                expiryRecord.setProductName(orderItem.getProductName());
                expiryRecordRepository.save(expiryRecord);
            }
        }
    }

    @Transactional
    public Result<ImportResultVO> importExcel(MultipartFile file) throws IOException {
        List<BarcodeStockRow> rowList = ExcelUtils.read(file, BarcodeStockRow.class);
        return Result.success(updateStock(rowList));
    }

    public ImportResultVO updateStock(List<BarcodeStockRow> rowList) {
        Map<String, Integer> stockMap = rowList.stream()
                .collect(Collectors.toMap(
                        BarcodeStockRow::getBarcode,
                        BarcodeStockRow::getStock
                ));
        int success = 0, skip = 0;
        ExpiryRecord expiryRecord = new ExpiryRecord();
        expiryRecord.setProcessStatus(false);
        List<ExpiryRecord> list = expiryRecordRepository.findAll(Example.of(expiryRecord));
        for (ExpiryRecord record : list) {
            if (stockMap.containsKey(record.getBarcode())) {
                record.setStock(stockMap.get(record.getBarcode()));
                success++;
            } else {
                skip++;
            }
        }
        expiryRecordRepository.saveAll(list);
        return new ImportResultVO(success, skip);
    }

    public Result<ExpiryRecordVO> findById(Long id) {
        ExpiryRecord expiryRecord = findEntityById(id);
        return Result.success(expiryRecordConverter.toVO(expiryRecord));
    }

    @Transactional
    public Result<Boolean> delete(Long id) {
        ExpiryRecord expiryRecord = findEntityById(id);
        expiryRecordRepository.delete(expiryRecord);
        return Result.success(true);
    }

    @Transactional
    public Result<Boolean> confirm(ExpiryConfirmRequest request) {
        ExpiryRecord expiryRecord = findEntityById(request.getId());
        expiryRecord.setConfirmStatus(true);
        expiryRecord.setStock(request.getStock());
        expiryRecord.setConfirmTime(LocalDateTime.now());
        expiryRecordRepository.save(expiryRecord);
        return Result.success(true);
    }

    private ExpiryRecord findEntityById(Long id) {
        return expiryRecordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
    }

    @Transactional
    public Result<Boolean> process(@Valid ExpiryProcessRequest request) {
        ExpiryRecord expiryRecord = findEntityById(request.getId());
        expiryRecord.setProcessStatus(true);
        expiryRecord.setProcessMethod(request.getProcessMethod());
        expiryRecord.setProcessRemark(request.getProcessRemark());
        expiryRecord.setProcessTime(LocalDateTime.now());
        expiryRecordRepository.save(expiryRecord);
        return Result.success(true);
    }

}
