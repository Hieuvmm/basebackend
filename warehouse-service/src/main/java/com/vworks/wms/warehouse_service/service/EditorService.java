package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.warehouse_service.entities.editsEntity.BannerEntity;
import com.vworks.wms.warehouse_service.entities.editsEntity.ContentEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface EditorService {
    // Banner APIs
    String uploadTempImage(MultipartFile file);

    List<BannerEntity> getBanners();
    BannerEntity uploadBanner(MultipartFile file, String label, int position);
    void updateBanner(Integer id, String label, Integer position);
    void deleteBanners(List<Integer> ids);

    List<ContentEntity> getAllContents();
    void saveAllData(List<Integer> bannerIds, List<ContentEntity> contents);
    void saveSingleContent(ContentEntity content);
    List<String> uploadTempImages(List<MultipartFile> files);

}
