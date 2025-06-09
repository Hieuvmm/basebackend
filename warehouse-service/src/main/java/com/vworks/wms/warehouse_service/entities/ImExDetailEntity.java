package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "im_ex_detail", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
public class ImExDetailEntity {
    @Id
    @Basic
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "times")
    private Integer times;
    @Basic
    @Column(name = "bill_code")
    private String billCode;
    @Basic
    @Column(name = "material_code")
    private String materialCode;
    @Basic
    @Column(name = "expected_quantity")
    private Integer expectedQuantity;
    @Basic
    @Column(name = "price")
    private BigDecimal price;
    @Basic
    @Column(name = "real_quantity")
    private Integer realQuantity;
    @Basic
    @Column(name = "material_type_code")
    private String materialTypeCode;
    @Basic
    @Column(name = "unit_type_code")
    private String unitTypeCode;
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

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Integer expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getRealQuantity() {
        return realQuantity;
    }

    public void setRealQuantity(Integer realQuantity) {
        this.realQuantity = realQuantity;
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

    public String getMaterialTypeCode() {
        return materialTypeCode;
    }

    public void setMaterialTypeCode(String materialTypeCode) {
        this.materialTypeCode = materialTypeCode;
    }

    public String getUnitTypeCode() {
        return unitTypeCode;
    }

    public void setUnitTypeCode(String unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImExDetailEntity that = (ImExDetailEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(times, that.times) && Objects.equals(billCode, that.billCode) && Objects.equals(materialCode, that.materialCode) && Objects.equals(expectedQuantity, that.expectedQuantity) && Objects.equals(price, that.price) && Objects.equals(realQuantity, that.realQuantity) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(materialTypeCode, that.materialTypeCode) && Objects.equals(unitTypeCode, that.unitTypeCode) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, times, billCode, materialCode, expectedQuantity, price, realQuantity, createdDate, updatedDate, createdBy, updatedBy, materialTypeCode, unitTypeCode, status);
    }
}
