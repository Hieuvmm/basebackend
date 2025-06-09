package com.vworks.wms.warehouse_service.repository;

import com.vworks.wms.warehouse_service.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    Page<OrderEntity> findAll(Specification<OrderEntity> specification, Pageable pageable);

    Optional<OrderEntity> findByCode(String code);

    List<OrderEntity> findAllByCodeIn(List<String> orderCodeList);

    Optional<OrderEntity> findFirstByCodeAndStatus(String code, String status);
}
