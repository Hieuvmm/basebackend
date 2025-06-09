package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

    boolean existsByCodeOrName(String code, String name);

    List<ProjectEntity> findAllByCodeIn(List<String> codes);

    Optional<ProjectEntity> findByCodeOrName(String code, String name);

    Page<ProjectEntity> findAll(Specification<ProjectEntity> spec, Pageable pageable);

}
