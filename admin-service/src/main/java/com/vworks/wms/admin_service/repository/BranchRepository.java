package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.BranchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {
    Boolean existsAllByCode(String code);

    BranchEntity findFirstByCodeOrId(String code, String id);

    Page<BranchEntity> findAll(Specification<BranchEntity> branchSpecification, Pageable pageable);
}
