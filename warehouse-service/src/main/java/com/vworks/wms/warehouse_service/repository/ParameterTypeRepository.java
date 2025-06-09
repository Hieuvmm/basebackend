package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ParameterTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterTypeRepository extends JpaRepository<ParameterTypeEntity, String> {
    Optional<ParameterTypeEntity> findByCodeOrName(String code, String name);

    Page<ParameterTypeEntity> findAll(Specification<ParameterTypeEntity> spec, Pageable pageable);
}
