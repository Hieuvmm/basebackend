package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.JobTitleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitleEntity, String> {
    JobTitleEntity findFirstByCodeOrId(String code, String id);

    Page<JobTitleEntity> findAll(Specification<JobTitleEntity> jobTitleEntitySpecification, Pageable pageable);
}
