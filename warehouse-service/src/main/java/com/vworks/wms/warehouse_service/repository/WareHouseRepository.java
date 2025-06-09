package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.WarehouseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WareHouseRepository extends JpaRepository<WarehouseEntity, String> {
    WarehouseEntity findFirstByCode(String id);

    Page<WarehouseEntity> findAllByStatus(Pageable pageable, String status);

    //    List<WarehouseEntity> findAllByCodeOrNameContains(Pageable pageable, String code, String name);
    Page<WarehouseEntity> findAllByCodeOrNameLike(Pageable pageable, String code, String name);

    Page<WarehouseEntity> findAllByCodeOrNameContainsAndStatus(Pageable pageable, String code, String name, String status);

    Optional<WarehouseEntity> findByCode(String code);

    Page<WarehouseEntity> findAll(Specification<WarehouseEntity> specification, Pageable pageable);
}
