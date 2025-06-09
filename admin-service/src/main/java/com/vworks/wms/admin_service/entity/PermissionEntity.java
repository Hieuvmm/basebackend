//package com.vworks.wms.admin_service.entity;
//
//import jakarta.persistence.*;
//
//import java.sql.Timestamp;
//import java.util.Objects;
//
//@Entity
//@Table(name = "permission", schema = "admin-service", catalog = "db-cuongphong-warehouse-mngt")
//public class PermissionEntity {
//    @Id
//    @Column(name = "id")
//    private String id;
//    @Basic
//    @Column(name = "code")
//    private String code;
//    @Basic
//    @Column(name = "name")
//    private String name;
//    @Basic
//    @Column(name = "endpoint")
//    private String endpoint;
//    @Basic
//    @Column(name = "module")
//    private String module;
//    @Basic
//    @Column(name = "description")
//    private String description;
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
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEndpoint() {
//        return endpoint;
//    }
//
//    public void setEndpoint(String endpoint) {
//        this.endpoint = endpoint;
//    }
//
//    public String getModule() {
//        return module;
//    }
//
//    public void setModule(String module) {
//        this.module = module;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
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
//        PermissionEntity that = (PermissionEntity) o;
//        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(endpoint, that.endpoint) && Objects.equals(module, that.module) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, code, name, endpoint, module, description, status, createdDate, updatedDate, createdBy, updatedBy);
//    }
//}
