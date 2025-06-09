package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.editsEntity.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Integer> {
    List<BannerEntity> findAllByOrderByPositionAsc();
}
