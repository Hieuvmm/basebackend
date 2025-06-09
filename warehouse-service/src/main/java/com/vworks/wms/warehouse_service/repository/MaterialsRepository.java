package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.MaterialsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialsRepository extends JpaRepository<MaterialsEntity, String> {
    Optional<MaterialsEntity> findByCodeOrName(String code, String name);

    Page<MaterialsEntity> findAll(Specification<MaterialsEntity> specification, Pageable pageable);

    Optional<MaterialsEntity> findFirstByCodeAndStatus(String code, String status);

    List<MaterialsEntity> findAllByCodeIn(List<String> codes);
}
