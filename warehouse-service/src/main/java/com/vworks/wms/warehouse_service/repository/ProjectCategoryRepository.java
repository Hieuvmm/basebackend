package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ProjectCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategoryEntity, String> {
    Page<ProjectCategoryEntity> findAll(Specification<ProjectCategoryEntity> spec, Pageable pageable);

    boolean existsByCodeOrName(String code, String name);

    Optional<ProjectCategoryEntity> findByCodeOrName(String code, String name);
}
