package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.UnitTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitTypeEntity, String> {

    Optional<UnitTypeEntity> findByCodeOrName(String code, String name);

    Page<UnitTypeEntity> findAll(Specification<UnitTypeEntity> spec, Pageable pageable);

    List<UnitTypeEntity> findAllByCodeIn(List<String> codes);
}
