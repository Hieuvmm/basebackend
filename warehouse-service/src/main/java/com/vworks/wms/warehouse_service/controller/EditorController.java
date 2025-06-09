package com.vworks.wms.warehouse_service.controller;

import com.vworks.wms.warehouse_service.config.WhsConstant;
import com.vworks.wms.warehouse_service.entities.editsEntity.BannerEntity;
import com.vworks.wms.warehouse_service.entities.editsEntity.ContentEntity;
import com.vworks.wms.warehouse_service.service.EditorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping(WhsConstant.RequestMapping.WHS_EDITOR)
public class EditorController {
    private final EditorService editorService;

    @PostMapping("/upload-image")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'upload_image', this)")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("file") List<MultipartFile> files) {
        try {
            List<String> urls = editorService.uploadTempImages(files);
            return ResponseEntity.ok(Map.of("urls", urls));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }


    // === Banner ===
    @GetMapping("/banners")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'banners', this)")
    public List<BannerEntity> getBanners() {
        return editorService.getBanners();
    }

    @PostMapping("/banners")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create_banner', this)")
    public BannerEntity uploadBanner(@RequestParam("file") MultipartFile file,
                                     @RequestParam String label,
                                     @RequestParam int position) {
        return editorService.uploadBanner(file, label, position);
    }

    @PutMapping("/banners/{id}")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update_banner', this)")
    public ResponseEntity<?> updateBanner(@PathVariable Integer id,
                                          @RequestParam(required = false) String label,
                                          @RequestParam(required = false) Integer position) {
        editorService.updateBanner(id, label, position);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/banners")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete_banner', this)")
    public ResponseEntity<?> deleteBanners(@RequestParam List<Integer> ids) {
        editorService.deleteBanners(ids);
        return ResponseEntity.ok().build();
    }

    // === Content ===
    @GetMapping("/contents")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'contents', this)")
    public List<ContentEntity> getAllContents() {
        return editorService.getAllContents();
    }

    @PostMapping("/contents/save-one")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'save_content', this)")
    public ResponseEntity<?> saveOneContent(@RequestBody ContentEntity content) {
        editorService.saveSingleContent(content);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contents/save-all")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'save_content', this)")
    public ResponseEntity<?> saveAll(@RequestParam List<Integer> bannerIds,
                                     @RequestBody List<ContentEntity> contents) {
        editorService.saveAllData(bannerIds, contents);
        return ResponseEntity.ok().build();
    }

}
