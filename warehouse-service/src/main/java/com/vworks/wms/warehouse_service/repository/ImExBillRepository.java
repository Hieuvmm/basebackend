package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ImExBillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImExBillRepository extends JpaRepository<ImExBillEntity, String> {
    Optional<ImExBillEntity> findFirstByCode(String code);

    Page findAllByStatus(Pageable pageable, String status);

    Page findFirstByCode(Pageable pageable, String code);

    Page<ImExBillEntity> findAll(Specification<ImExBillEntity> spec, Pageable pageable);

    Optional<ImExBillEntity> findFirstByCodeAndStatus(String code, String status);

    Optional<ImExBillEntity> findByCode(String code);

    List<ImExBillEntity> findAllByCodeIn(List<String> codes);

    boolean existsByCode(String code);
}
