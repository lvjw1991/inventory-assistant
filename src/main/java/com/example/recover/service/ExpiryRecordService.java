package com.example.recover.service;

import com.example.recover.dto.Result;
import com.example.recover.entity.ExpiryRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpiryRecordService {


    public Result<Page<ExpiryRecord>> search(int pageNum, int pageSize) {
        return null;
    }

    public Result<ExpiryRecord> findById(Long id) {
        return null;
    }
}
