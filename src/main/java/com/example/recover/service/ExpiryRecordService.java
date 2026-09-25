package com.example.recover.service;

import com.example.recover.dto.*;
import com.example.recover.entity.Product;
import com.example.recover.entity.ReceivingOrderItem;
import com.example.recover.exception.BusinessException;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.repository.ExpiryRecordRepository;
import com.example.recover.repository.ProductRepository;
import com.example.recover.utils.*;
import com.example.recover.vo.*;
import com.example.recover.entity.ExpiryRecord;
import lombok.RequiredArgsConstructor;
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

    private final ProductRepository productRepository;

    private final ExpiryRecordDetailConverter detailConverter;

    public Result<PageResponse<ExpiryRecordVO>> search(RecordQuery query) {
        Pageable pageable = PageRequest.of(
                query.getPageNum(), query.getPageSize(),
                Sort.by("expiryDate").ascending());
        return Result.success(PageResponse.of(expiryRecordRepository.findPage(query.getExpireDateFrom(),
                query.getExpireDateTo(), query.getConfirmStatus(), query.getProcessStatus(),
                query.getCategory(), query.getBarcode(), pageable)));
    }

    /**
     * 只更新下个月未确认的库存数据
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Transactional
    public Result<ImportResultVO> importExcel(MultipartFile file) throws IOException {
        List<BarcodeStockRow> rowList = ExcelUtils.read(file, BarcodeStockRow.class);
        return Result.success(updateStock(rowList));
    }

    public ImportResultVO updateStock(List<BarcodeStockRow> rowList) {
        Map<String, Integer> stockMap = null;
        try {
            stockMap = rowList.stream()
                    .collect(Collectors.toMap(
                            BarcodeStockRow::getBarcode,
                            BarcodeStockRow::getStock
                    ));
        } catch (IllegalStateException e) {
            throw new BusinessException(500, "Excel 中存在重复 barcode");
        }
        int success = 0, skip = 0;
        LocalDate start = LocalDate.now()
                .plusMonths(1)
                .withDayOfMonth(1);
        LocalDate end = LocalDate.now()
                .plusMonths(2)
                .withDayOfMonth(1);
        List<ExpiryRecord> list = expiryRecordRepository.findByConfirmStatusAndExpiryDateGreaterThanEqualAndExpiryDateLessThan(
                ConfirmStatus.UNCONFIRM, start, end);
        if (list.isEmpty()) {
            return new ImportResultVO(success, skip);
        }
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

    public Result<ExpiryRecordDetailVO> findById(Long id) {
        ExpiryRecord expiryRecord = findEntityById(id);
        ExpiryRecordDetailVO vo = detailConverter.toVO(expiryRecord);
        Product byBarcode = productRepository.findByBarcode(vo.getBarcode());
        if (byBarcode != null) {
            vo.setImgUrl(byBarcode.getImgUrl());
        }
        vo.setOtherDateList(expiryRecordRepository.findOtherExpiryDates(expiryRecord.getBarcode(), LocalDate.now(), id));
        return Result.success(vo);
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
        if (ConfirmStatus.UNCONFIRM.equals(expiryRecord.getConfirmStatus())) {
            return Result.fail(500, "商品尚未确认，不能进行处理");
        }
        expiryRecord.setProcessStatus(request.getProcessStatus());
        expiryRecord.setProcessRemark(request.getProcessRemark());
        expiryRecord.setProcessTime(LocalDateTime.now());
        expiryRecord.setStock(request.getStock());
        expiryRecordRepository.save(expiryRecord);
        return Result.success(true);
    }

    /**
     * batch
     *
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
            return Result.fail(500, "barcode, date重复, 无需新增");
        }
        ExpiryRecord expiryRecord = new ExpiryRecord();
        expiryRecord.setBarcode(barcode);
        expiryRecord.setExpiryDate(date);
        expiryRecord.setCategory(request.getCategory());
        expiryRecord.setConfirmStatus(ConfirmStatus.CONFIRM);
        expiryRecord.setConfirmTime(LocalDateTime.now());
        expiryRecord.setProcessStatus(ProcessStatus.UNPROCESS);
        expiryRecord.setProductName(getProductNameByBarcode(barcode));
        expiryRecord.setStock(request.getStock());
        return Result.success(expiryRecordConverter.toVO(expiryRecordRepository.save(expiryRecord)));
    }

    private String getProductNameByBarcode(String barcode) {
        Product byBarcode = productRepository.findByBarcode(barcode);
        if (byBarcode != null) {
            return byBarcode.getName();
        }
        return "";
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
        expiryRecord.setProductName(getProductNameByBarcode(barcode));
        expiryRecord.setStock(request.getStock());
        return Result.success(expiryRecordConverter.toVO(expiryRecordRepository.save(expiryRecord)));
    }

    public Result<List<ExpiryRecordVO>> searchMonthly(RecordMonthlyQuery query) {
        LocalDate expireDateFrom = query.getExpireDateFrom();
        LocalDate expireDateTo = query.getExpireDateTo();
        if (ChronoUnit.DAYS.between(expireDateFrom, expireDateTo) > 60) {
            return Result.fail(500, "请用分页接口");
        }
        return Result.success(expiryRecordRepository.findMonthly(query.getExpireDateFrom(),
                query.getExpireDateTo(), query.getConfirmStatus(), query.getProcessStatus(),
                query.getCategory()));
    }
}
