package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String> {
    Boolean existsAllByCode(String code);

    DepartmentEntity findFirstByCodeOrId(String code, String id);

    Page<DepartmentEntity> findAll(Specification<DepartmentEntity> specification, Pageable pageable);

    List<DepartmentEntity> findAllByCodeIn(List<String> departmentCodes);
}
