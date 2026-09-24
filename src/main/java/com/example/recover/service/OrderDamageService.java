package com.example.recover.service;

import com.example.recover.entity.ReceivingDamage;
import com.example.recover.repository.ReceivingDamageRepository;
import com.example.recover.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDamageService {

    private final ReceivingDamageRepository damageRepository;

    @Value("${file.upload.damage-path}")
    private String damagePath;

    @Value("${file.upload.max-size}")
    private DataSize maxSize;


    public Result<List<String>> findByItemId(Long itemId) {
        List<ReceivingDamage> damageList = damageRepository.findByReceivingOrderItemId(itemId);
        List<String> imgList = damageList.stream().map(ReceivingDamage::getImgUrl).toList();
        return Result.success(imgList);
    }

    public Result<String> importImg(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.fail(500,"文件不能为空");
        }
        if (file.getSize() > maxSize.toBytes()) {
            return Result.fail(500,"文件大小不能超过 " + maxSize);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.fail(500,"只能上传图片");
        }
        try {
            Path uploadDir = Paths.get(damagePath);
            Files.createDirectories(uploadDir);
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null) {
                int index = originalFilename.lastIndexOf(".");
                if (index >= 0) {
                    extension = originalFilename.substring(index);
                }
            }
            String filename = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(filename);
            file.transferTo(target);
            String url = "/ia/uploads/damage/" + filename;
            return Result.success(url);
        } catch (IOException e) {
            return Result.fail(500,"图片上传失败");
        }
    }

    @Transactional
    public Result<Boolean> upsert(List<String> imgList, Long itemId) {
        List<ReceivingDamage> damageList = damageRepository.findByReceivingOrderItemId(itemId);
        Set<String> newUrls = imgList == null
                ? Collections.emptySet()
                : new HashSet<>(imgList);
        // 1. 删除数据库中已经不在前端列表里的记录
        List<ReceivingDamage> deleteList = damageList.stream()
                .filter(damage -> !newUrls.contains(damage.getImgUrl()))
                .toList();

        if (!deleteList.isEmpty()) {
            damageRepository.deleteAll(deleteList);
        }
        // 2. 新增数据库中不存在的记录
        Set<String> oldUrls = damageList.stream()
                .map(ReceivingDamage::getImgUrl)
                .collect(Collectors.toSet());
        for (String imgUrl : newUrls) {
            if (!oldUrls.contains(imgUrl)) {
                ReceivingDamage damage = new ReceivingDamage();
                damage.setImgUrl(imgUrl);
                damage.setReceivingOrderItemId(itemId);
                damageRepository.save(damage);
            }
        }
        return Result.success(true);
    }

}
