//package com.vworks.wms.admin_service.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//
//import java.sql.Timestamp;
//import java.util.Objects;
//
//@Entity
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "auth_token", schema = "admin-service", catalog = "db-cuongphong-warehouse-mngt")
//public class AuthTokenEntity {
//    @Id
//    @Column(name = "id")
//    private String id;
//    @Basic
//    @Column(name = "user_id")
//    private String userId;
//    @Basic
//    @Column(name = "user_code")
//    private String userCode;
//    @Basic
//    @Column(name = "username")
//    private String username;
//    @Basic
//    @Column(name = "type_token")
//    private String typeToken;
//    @Basic
//    @Column(name = "token")
//    private String token;
//    @Basic
//    @Column(name = "expire_at")
//    private Timestamp expireAt;
//    @Basic
//    @Column(name = "platform")
//    private String platform;
//    @Basic
//    @Column(name = "device_id")
//    private String deviceId;
//    @Basic
//    @Column(name = "location")
//    private String location;
//    @Basic
//    @Column(name = "status")
//    private String status;
//    @Basic
//    @Column(name = "created_date")
//    private Timestamp createdDate;
//    @Basic
//    @Column(name = "updated_date")
//    private Timestamp updatedDate;
//    @Basic
//    @Column(name = "created_by")
//    private String createdBy;
//    @Basic
//    @Column(name = "updated_by")
//    private String updatedBy;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getUserCode() {
//        return userCode;
//    }
//
//    public void setUserCode(String userCode) {
//        this.userCode = userCode;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getTypeToken() {
//        return typeToken;
//    }
//
//    public void setTypeToken(String typeToken) {
//        this.typeToken = typeToken;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public Timestamp getExpireAt() {
//        return expireAt;
//    }
//
//    public void setExpireAt(Timestamp expireAt) {
//        this.expireAt = expireAt;
//    }
//
//    public String getPlatform() {
//        return platform;
//    }
//
//    public void setPlatform(String platform) {
//        this.platform = platform;
//    }
//
//    public String getDeviceId() {
//        return deviceId;
//    }
//
//    public void setDeviceId(String deviceId) {
//        this.deviceId = deviceId;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Timestamp getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(Timestamp createdDate) {
//        this.createdDate = createdDate;
//    }
//
//    public Timestamp getUpdatedDate() {
//        return updatedDate;
//    }
//
//    public void setUpdatedDate(Timestamp updatedDate) {
//        this.updatedDate = updatedDate;
//    }
//
//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getUpdatedBy() {
//        return updatedBy;
//    }
//
//    public void setUpdatedBy(String updatedBy) {
//        this.updatedBy = updatedBy;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        AuthTokenEntity that = (AuthTokenEntity) o;
//        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(userCode, that.userCode) && Objects.equals(username, that.username) && Objects.equals(typeToken, that.typeToken) && Objects.equals(token, that.token) && Objects.equals(expireAt, that.expireAt) && Objects.equals(platform, that.platform) && Objects.equals(deviceId, that.deviceId) && Objects.equals(location, that.location) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, userId, userCode, username, typeToken, token, expireAt, platform, deviceId, location, status, createdDate, updatedDate, createdBy, updatedBy);
//    }
//}
