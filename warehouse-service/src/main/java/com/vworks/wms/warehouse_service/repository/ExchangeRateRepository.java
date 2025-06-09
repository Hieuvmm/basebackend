package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ExchangeRateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, String> {
    Optional<ExchangeRateEntity> findByCodeOrName(String code, String name);

    Page<ExchangeRateEntity> findAll(Specification<ExchangeRateEntity> spec, Pageable pageable);
}
