package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findFirstByUsernameOrUserCodeOrUserId(String username, String userCode, String userId);

    Optional<UserEntity> findFirstByUserCodeAndStatus(String userCode, String status);

    int countAllByUserCodeStartsWith(String userCode);

    List<UserEntity> findAllByUserInfoIdIn(List<String> userInfoIds, Pageable pageable);

    Boolean existsByUserIdIsOrUsernameIs(String userId, String username);

    List<UserEntity> findAllByStatus(String status);

    List<UserEntity> findAllByStatusAndRoleCode(String status, String Role);
}
