package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ProfessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessionRepository extends JpaRepository<ProfessionEntity, String> {

    Optional<ProfessionEntity> findByCodeOrName(String code, String name);

    Page<ProfessionEntity> findAll(Specification<ProfessionEntity> spec, Pageable pageable);
}
