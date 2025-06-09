//package com.vworks.wms.admin_service.repository;
//
//import com.vworks.wms.admin_service.entity.AuthTokenEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.sql.Timestamp;
//import java.util.List;
//
//@Repository
//public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, String> {
//    List<AuthTokenEntity> findAllByUserCodeAndStatus(String userCode, String status);
//
//    List<AuthTokenEntity> findAllByUserCodeAndStatusAndExpireAtBefore(String userCode, String status, Timestamp conditionExpired);
//
//    AuthTokenEntity findFirstByToken(String token);
//}
