package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "object", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
public class ObjectEntity {
    private String id;
    private String code;
    private String name;
    private String type;
    private String phoneNumber;
    private Integer provinceCode;
    private Integer districtCode;
    private String addressDetail;
    private String professionCode;
    private String agentLevelCode;
    private String taxCode;
    private String maximumDebt;
    private String debtDay;
    private String bankName;
    private String bankNumber;
    private String businessManagerCode;
    private String description;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
    private String accountName;

    @Basic
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "account_name")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "province_code")
    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Basic
    @Column(name = "district_code")
    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    @Basic
    @Column(name = "address_detail")
    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    @Basic
    @Column(name = "profession_code")
    public String getProfessionCode() {
        return professionCode;
    }

    public void setProfessionCode(String professionCode) {
        this.professionCode = professionCode;
    }

    @Basic
    @Column(name = "agent_level_code")
    public String getAgentLevelCode() {
        return agentLevelCode;
    }

    public void setAgentLevelCode(String agentLevelCode) {
        this.agentLevelCode = agentLevelCode;
    }

    @Basic
    @Column(name = "tax_code")
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Basic
    @Column(name = "maximum_debt")
    public String getMaximumDebt() {
        return maximumDebt;
    }

    public void setMaximumDebt(String maximumDebt) {
        this.maximumDebt = maximumDebt;
    }

    @Basic
    @Column(name = "debt_day")
    public String getDebtDay() {
        return debtDay;
    }

    public void setDebtDay(String debtDay) {
        this.debtDay = debtDay;
    }

    @Basic
    @Column(name = "bank_name")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Basic
    @Column(name = "bank_number")
    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    @Basic
    @Column(name = "business_manager_code")
    public String getBusinessManagerCode() {
        return businessManagerCode;
    }

    public void setBusinessManagerCode(String businessManagerCode) {
        this.businessManagerCode = businessManagerCode;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "created_date")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Basic
    @Column(name = "updated_date")
    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Basic
    @Column(name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Basic
    @Column(name = "updated_by")
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
        ObjectEntity that = (ObjectEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(provinceCode, that.provinceCode) && Objects.equals(districtCode, that.districtCode) && Objects.equals(addressDetail, that.addressDetail) && Objects.equals(professionCode, that.professionCode) && Objects.equals(agentLevelCode, that.agentLevelCode) && Objects.equals(taxCode, that.taxCode) && Objects.equals(maximumDebt, that.maximumDebt) && Objects.equals(debtDay, that.debtDay) && Objects.equals(bankName, that.bankName) && Objects.equals(bankNumber, that.bankNumber) && Objects.equals(businessManagerCode, that.businessManagerCode) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, type, phoneNumber, provinceCode, districtCode, addressDetail, professionCode, agentLevelCode, taxCode, maximumDebt, debtDay, bankName, bankNumber, businessManagerCode, description, status, createdDate, updatedDate, createdBy, updatedBy);
    }
}
