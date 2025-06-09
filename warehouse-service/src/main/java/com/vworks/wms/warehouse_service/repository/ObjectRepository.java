package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ObjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObjectRepository extends JpaRepository<ObjectEntity, String> {
    Optional<ObjectEntity> findByCodeOrName(String code, String name);

    Page<ObjectEntity> findAll(Specification<ObjectEntity> specification, Pageable pageable);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByCodeAndType(String code, String type);
}
