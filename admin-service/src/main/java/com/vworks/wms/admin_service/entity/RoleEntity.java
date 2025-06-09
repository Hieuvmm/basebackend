package com.vworks.wms.admin_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role", schema = "admin-service", catalog = "db-cuongphong-warehouse-mngt")
public class RoleEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "code")
    private String code;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "type")
    private String type;
    @Basic
    @Column(name = "scope")
    private Object scope;
    @Basic
    @Column(name = "applicable")
    private Object applicable;
    @Basic
    @Column(name = "resource_access")
    private Object resourceAccess;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "created_date")
    private Timestamp createdDate;
    @Basic
    @Column(name = "updated_date")
    private Timestamp updatedDate;
    @Basic
    @Column(name = "created_by")
    private String createdBy;
    @Basic
    @Column(name = "updated_by")
    private String updatedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getScope() {
        return scope;
    }

    public void setScope(Object scope) {
        this.scope = scope;
    }

    public Object getApplicable() {
        return applicable;
    }

    public void setApplicable(Object applicable) {
        this.applicable = applicable;
    }

    public Object getResourceAccess() {
        return resourceAccess;
    }

    public void setResourceAccess(Object resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity that = (RoleEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(scope, that.scope) && Objects.equals(applicable, that.applicable) && Objects.equals(resourceAccess, that.resourceAccess) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, type, scope, applicable, resourceAccess, description, status, createdDate, updatedDate, createdBy, updatedBy);
    }
}
