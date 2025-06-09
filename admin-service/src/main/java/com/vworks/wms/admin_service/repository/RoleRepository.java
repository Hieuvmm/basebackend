package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    Boolean existsAllByCode(String code);

    RoleEntity findFirstByIdOrCode(String id, String code);

    Page<RoleEntity> findAll(Specification<RoleEntity> specification, Pageable pageable);
}
