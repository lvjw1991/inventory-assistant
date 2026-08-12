package com.example.recover.service;

import com.example.recover.dto.*;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ExpiryRecordRepository;
import com.example.recover.utils.ConfirmStatus;
import com.example.recover.utils.ExcelUtils;
import com.example.recover.utils.ExpiryRecordConverter;
import com.example.recover.utils.ProcessStatus;
import com.example.recover.vo.*;
import com.example.recover.entity.ExpiryRecord;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpiryRecordService {

    private final ExpiryRecordRepository expiryRecordRepository;

    private final ExpiryRecordConverter expiryRecordConverter;

    public Result<PageResponse<ExpiryRecordVO>> search(RecordQuery query) {
        Pageable pageable = PageRequest.of(
                query.getPageNum(), query.getPageSize(),
                Sort.by("expiryDate").ascending());
        return Result.success(PageResponse.of(expiryRecordRepository.findPage(query.getExpireDateFrom(),
                query.getExpireDateTo(), query.getConfirmStatus(), query.getProcessStatus(),
                query.getCategory(), query.getBarcode(), pageable)));
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
        expiryRecord.setConfirmStatus(ConfirmStatus.UNCONFIRM);
        List<ExpiryRecord> list = expiryRecordRepository.findAll(Example.of(expiryRecord));
        List<ExpiryRecord> updateList = new ArrayList<>();
        for (ExpiryRecord record : list) {
            if (stockMap.containsKey(record.getBarcode())) {
                record.setStock(stockMap.get(record.getBarcode()));
                updateList.add(record);
                success++;
            } else {
                skip++;
            }
        }
        expiryRecordRepository.saveAll(updateList);
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
        expiryRecord.setConfirmStatus(request.getConfirmStatus());
        expiryRecord.setStock(request.getStock());
        expiryRecord.setConfirmTime(LocalDateTime.now());
        expiryRecordRepository.save(expiryRecord);
        return Result.success(true);
    }

    private ExpiryRecord findEntityById(Long id) {
        return expiryRecordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
    }

    @Transactional
    public Result<Boolean> process(ExpiryProcessRequest request) {
        ExpiryRecord expiryRecord = findEntityById(request.getId());
        if(ConfirmStatus.UNCONFIRM.equals(expiryRecord.getConfirmStatus())){
            return Result.fail(500, "商品尚未确认，不能进行处理");
        }
        expiryRecord.setProcessStatus(request.getProcessStatus());
        expiryRecord.setProcessRemark(request.getProcessRemark());
        expiryRecord.setProcessTime(LocalDateTime.now());
        expiryRecordRepository.save(expiryRecord);
        return Result.success(true);
    }

    /**
     * batch
     * @param orderItemList
     */
    @Transactional
    public void saveAll(List<ReceivingOrderItem> orderItemList) {
        List<String> barcodeList = orderItemList.stream().map(ReceivingOrderItem::getBarcode).distinct().toList();
        List<ExpiryRecord> expiryRecordList = expiryRecordRepository.findByBarcodeIn(barcodeList);
        Set<String> existingKeys = expiryRecordList.stream()
                .map(p -> p.getBarcode() + ":" + p.getExpiryDate())
                .collect(Collectors.toSet());
        List<ExpiryRecord> saveList = new ArrayList<>();
        for (ReceivingOrderItem item : orderItemList) {
            String expiryDate = item.getExpiryDate();
            String barcode = item.getBarcode();
            if (expiryDate == null || expiryDate.isBlank()
                    || barcode == null || barcode.isBlank()) {
                continue;
            }
            String[] dateArray = expiryDate.split(",");
            for (String dateString : dateArray) {
                LocalDate date = LocalDate.parse(dateString);
                String key = barcode + ":" + date;
                // 数据库已有，跳过
                if (existingKeys.contains(key)) {
                    continue;
                }
                ExpiryRecord expiryRecord = new ExpiryRecord();
                expiryRecord.setBarcode(barcode);
                expiryRecord.setExpiryDate(date);
                expiryRecord.setCategory(item.getCategory());
                expiryRecord.setConfirmStatus(ConfirmStatus.UNCONFIRM);
                expiryRecord.setProcessStatus(ProcessStatus.UNPROCESS);
                expiryRecord.setProductName(item.getProductName());
                saveList.add(expiryRecord);
                // 加入 Set，防止本次 Excel 自己重复
                existingKeys.add(key);
            }
        }
        if (!saveList.isEmpty()) {
            expiryRecordRepository.saveAll(saveList);
        }

    }

    @Transactional
    public Result<ExpiryRecordVO> create(ExpiryRecordRequest request) {
        String barcode = request.getBarcode();
        LocalDate date = request.getExpiryDate();
        boolean exist = expiryRecordRepository.existsByBarcodeAndExpiryDate(barcode, date);
        if (exist) {
            return Result.fail(500, "barcode, date重复");
        }
        ExpiryRecord expiryRecord = new ExpiryRecord();
        expiryRecord.setBarcode(barcode);
        expiryRecord.setExpiryDate(date);
        expiryRecord.setCategory(request.getCategory());
        expiryRecord.setConfirmStatus(ConfirmStatus.UNCONFIRM);
        expiryRecord.setProcessStatus(ProcessStatus.UNPROCESS);
        expiryRecord.setProductName(request.getProductName());
        return Result.success(expiryRecordConverter.toVO(expiryRecordRepository.save(expiryRecord)));
    }

    @Transactional
    public Result<ExpiryRecordVO> update(ExpiryRecordRequest request) {
        ExpiryRecord expiryRecord = findEntityById(request.getId());
        String barcode = request.getBarcode();
        LocalDate date = request.getExpiryDate();
        boolean exist = expiryRecordRepository.existsByBarcodeAndExpiryDate(barcode, date);
        if (exist) {
            return Result.fail(500, "barcode, date重复");
        }
        expiryRecord.setBarcode(barcode);
        expiryRecord.setExpiryDate(date);
        expiryRecord.setCategory(request.getCategory());
        expiryRecord.setProductName(request.getProductName());
        return Result.success(expiryRecordConverter.toVO(expiryRecordRepository.save(expiryRecord)));
    }

    public Result<List<ExpiryRecordVO>> searchMonthly(RecordMonthlyQuery query) {
        LocalDate expireDateFrom = query.getExpireDateFrom();
        LocalDate expireDateTo = query.getExpireDateTo();
        if(ChronoUnit.DAYS.between(expireDateFrom, expireDateTo) > 60){
            return Result.fail(500, "请用分页接口");
        }
        return Result.success(expiryRecordRepository.findMonthly(query.getExpireDateFrom(),
                query.getExpireDateTo(), query.getConfirmStatus(), query.getProcessStatus(),
                query.getCategory()));
    }
}
