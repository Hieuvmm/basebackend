package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "detail_materials", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailMaterialsEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "material_type_code")
    private String materialTypeCode;
    @Basic
    @Column(name = "code")
    private String code;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "parameters")
    private String parameters;
    @Basic
    @Column(name = "measure_keyword")
    private String measureKeyword;
    @Basic
    @Column(name = "sell_price")
    private BigDecimal sellPrice;
    @Basic
    @Column(name = "list_price")
    private BigDecimal listPrice;
    @Basic
    @Column(name = "origin")
    private String origin;
    @Basic
    @Column(name = "min_inventory")
    private Long minInventory;
    @Basic
    @Column(name = "provider_code")
    private String providerCode;
    @Basic
    @Column(name = "discount")
    private String discount;
    @Basic
    @Column(name = "image")
    private String image;
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

    public String getMaterialTypeCode() {
        return materialTypeCode;
    }

    public void setMaterialTypeCode(String materialTypeCode) {
        this.materialTypeCode = materialTypeCode;
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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getMeasureKeyword() {
        return measureKeyword;
    }

    public void setMeasureKeyword(String measureKeyword) {
        this.measureKeyword = measureKeyword;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getMinInventory() {
        return minInventory;
    }

    public void setMinInventory(Long minInventory) {
        this.minInventory = minInventory;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        DetailMaterialsEntity that = (DetailMaterialsEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(materialTypeCode, that.materialTypeCode) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(parameters, that.parameters) && Objects.equals(measureKeyword, that.measureKeyword) && Objects.equals(listPrice, that.listPrice) && Objects.equals(sellPrice, that.sellPrice) && Objects.equals(origin, that.origin) && Objects.equals(minInventory, that.minInventory) && Objects.equals(providerCode, that.providerCode) && Objects.equals(discount, that.discount) && Objects.equals(image, that.image) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, materialTypeCode, code, name, parameters, measureKeyword, sellPrice, listPrice, origin, minInventory, providerCode, discount, image, description, status, createdDate, updatedDate, createdBy, updatedBy);
    }
}
