package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, String> {
    Optional<ProviderEntity> findByCodeOrName(String code, String name);

    Page<ProviderEntity> findAll(Specification<ProviderEntity> spec, Pageable pageable);

    Optional<ProviderEntity> findFirstByCode(String code);
}
