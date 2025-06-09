package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.DetailMaterialsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailMaterialsRepository extends JpaRepository<DetailMaterialsEntity, String> {
    Optional<DetailMaterialsEntity> findByCodeOrName(String code, String name);

    Page<DetailMaterialsEntity> findAll(Specification<DetailMaterialsEntity> spec, Pageable pageable);

    List<DetailMaterialsEntity> findAllByMaterialTypeCode(String materialTypeCode);

    Optional<DetailMaterialsEntity> findFirstByCode(String code);

    List<DetailMaterialsEntity> findAllByCodeIn(List<String> codes);
}
