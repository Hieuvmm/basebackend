package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.config.MinioConfigProperties;
import com.vworks.wms.common_lib.service.MinioService;
import com.vworks.wms.warehouse_service.entities.editsEntity.BannerEntity;
import com.vworks.wms.warehouse_service.entities.editsEntity.ContentEntity;
import com.vworks.wms.warehouse_service.repository.BannerRepository;
import com.vworks.wms.warehouse_service.repository.ContentRepository;
import com.vworks.wms.warehouse_service.service.EditorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@CrossOrigin("*")
public class EditorServiceImpl implements EditorService {

    private final BannerRepository bannerRepository;
    private final ContentRepository contentRepository;
    private final MinioService minioService;
    private final MinioConfigProperties minioConfig;

    @Override
    public String uploadTempImage(MultipartFile file) {
        try {
            String folder = minioConfig.getMaterialImageFolderStorage();
            String bucket = minioConfig.getBucketName();
            String path = String.format("%s/temp/%d_%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
            return minioService.uploadFileToMinio(file, bucket, path);
        } catch (Exception e) {
            throw new RuntimeException("Upload image failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BannerEntity> getBanners() {
        return bannerRepository.findAllByOrderByPositionAsc();
    }

    @Override
    public BannerEntity uploadBanner(MultipartFile file, String label, int position) {
        try {
            String folder = minioConfig.getMaterialImageFolderStorage();
            String bucket = minioConfig.getBucketName();
            String path = String.format("%s/banner/%d_%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
            String imageUrl = minioService.uploadFileToMinio(file, bucket, path);

            BannerEntity banner = BannerEntity.builder()
                    .imageUrl(imageUrl)
                    .label(label)
                    .position(position)
                    .build();

            return bannerRepository.save(banner);
        } catch (Exception e) {
            throw new RuntimeException("Upload banner failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateBanner(Integer id, String label, Integer position) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        if (label != null) banner.setLabel(label);
        if (position != null) banner.setPosition(position);

        bannerRepository.save(banner);
    }

    @Override
    public void deleteBanners(List<Integer> ids) {
        List<BannerEntity> banners = bannerRepository.findAllById(ids);
        bannerRepository.deleteAll(banners);
    }

    @Override
    public List<ContentEntity> getAllContents() {
        return contentRepository.findAll();
    }

    @Override
    public void saveAllData(List<Integer> bannerIds, List<ContentEntity> contents) {
        List<BannerEntity> all = bannerRepository.findAll();
        for (BannerEntity banner : all) {
            if (!bannerIds.contains(banner.getId())) {
                bannerRepository.delete(banner);
            }
        }

        for (ContentEntity content : contents) {
            ContentEntity existing = contentRepository.findByPosition(content.getPosition())
                    .orElse(ContentEntity.builder().position(content.getPosition()).build());

            existing.setTitle(content.getTitle());
            existing.setBody(content.getBody());
            existing.setType(content.getType());
            existing.setDate(content.getDate());
            existing.setBadge(content.getBadge());
            existing.setImageUrls(content.getImageUrls());

            contentRepository.save(existing);
        }
    }

    @Override
    public void saveSingleContent(ContentEntity content) {
        ContentEntity existing = contentRepository.findByPosition(content.getPosition())
                .orElse(ContentEntity.builder().position(content.getPosition()).build());

        existing.setTitle(content.getTitle());
        existing.setBody(content.getBody());
        existing.setType(content.getType());
        existing.setDate(content.getDate());
        existing.setBadge(content.getBadge());
        existing.setImageUrls(content.getImageUrls());

        contentRepository.save(existing);
    }
    @Override
    public List<String> uploadTempImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        String folder = minioConfig.getMaterialImageFolderStorage();
        String bucket = minioConfig.getBucketName();

        for (MultipartFile file : files) {
            try {
                String path = String.format("%s/temp/%d_%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
                String url = minioService.uploadFileToMinio(file, bucket, path);
                urls.add(url);
            } catch (Exception e) {
                throw new RuntimeException("Upload failed: " + file.getOriginalFilename(), e);
            }
        }
        return urls;
    }

}
