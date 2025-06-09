package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.JobPositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPositionEntity, String> {
    JobPositionEntity findFirstByCodeOrId(String code, String id);

    Page<JobPositionEntity> findAll(Specification<JobPositionEntity> jobPositionEntitySpecification, Pageable pageable);
}
