package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ProjectTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectTypeEntity, String> {

    Page<ProjectTypeEntity> findAll(Specification<ProjectTypeEntity> spec, Pageable pageable);

    boolean existsByCodeOrName(String code, String name);

    Optional<ProjectTypeEntity> findByCodeOrName(String code, String name);

    List<ProjectTypeEntity> findAllByCodeIn(List<String> codes);
}
