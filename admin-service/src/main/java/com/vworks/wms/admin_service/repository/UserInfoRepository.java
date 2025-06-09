package com.vworks.wms.admin_service.repository;

import com.vworks.wms.admin_service.entity.UserInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {
    Optional<UserInfoEntity> findFirstByUsername(String username);

    Optional<UserInfoEntity> findFirstByUserCodeOrUserId(String userCode, String userId);

    Page<UserInfoEntity> findAll(Specification<UserInfoEntity> userInfoEntitySpecification, Pageable pageable);

    boolean existsByUserId(String userId);

    boolean existsByUserCode(String userCode);

    Optional<UserInfoEntity> findByUserId(String userId);

    List<UserInfoEntity> findByUserIdIn(List<String> userIds);

    boolean existsByUserIdIsOrUsernameIsOrEmailIs(String userId, String username, String email);
}
