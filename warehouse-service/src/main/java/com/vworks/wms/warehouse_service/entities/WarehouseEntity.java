package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "warehouse", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
public class WarehouseEntity {
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
    @Column(name = "phone_number_wh")
    private String phoneNumberWh;
    @Basic
    @Column(name = "address_wh")
    private String addressWh;
    @Basic
    @Column(name = "manager_wh")
    private String managerWh;
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

    public String getPhoneNumberWh() {
        return phoneNumberWh;
    }

    public void setPhoneNumberWh(String phoneNumberWh) {
        this.phoneNumberWh = phoneNumberWh;
    }

    public String getAddressWh() {
        return addressWh;
    }

    public void setAddressWh(String addressWh) {
        this.addressWh = addressWh;
    }

    public String getManagerWh() {
        return managerWh;
    }

    public void setManagerWh(String managerWh) {
        this.managerWh = managerWh;
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
        WarehouseEntity that = (WarehouseEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(phoneNumberWh, that.phoneNumberWh) && Objects.equals(addressWh, that.addressWh) && Objects.equals(managerWh, that.managerWh) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, phoneNumberWh, addressWh, managerWh, description, status, createdDate, updatedDate, createdBy, updatedBy);
    }
}
