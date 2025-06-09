package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "project", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
public class ProjectEntity {
    private String id;
    private String code;
    private String name;
    private String projectTypeCode;
    private String customerCode;
    private String startDate;
    private String endDate;
    private Integer provinceCode;
    private Integer districtCode;
    private String addressDetail;
    private String supervisorCode;
    private String supervisorPhone;
    private String technicianCode;
    private String technicianPhone;
    private String otherInfo;
    private String categoryInfo;
    private String actionDetail;
    private String approval;
    private String follow;
    private String note;
    private String status;
    private String attachments;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;

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
    @Column(name = "project_type_code")
    public String getProjectTypeCode() {
        return projectTypeCode;
    }

    public void setProjectTypeCode(String projectTypeCode) {
        this.projectTypeCode = projectTypeCode;
    }

    @Basic
    @Column(name = "customer_code")
    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @Basic
    @Column(name = "start_date")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
    @Column(name = "supervisor_code")
    public String getSupervisorCode() {
        return supervisorCode;
    }

    public void setSupervisorCode(String supervisorCode) {
        this.supervisorCode = supervisorCode;
    }

    @Basic
    @Column(name = "supervisor_phone")
    public String getSupervisorPhone() {
        return supervisorPhone;
    }

    public void setSupervisorPhone(String supervisorPhone) {
        this.supervisorPhone = supervisorPhone;
    }

    @Basic
    @Column(name = "technician_code")
    public String getTechnicianCode() {
        return technicianCode;
    }

    public void setTechnicianCode(String technicianCode) {
        this.technicianCode = technicianCode;
    }

    @Basic
    @Column(name = "technician_phone")
    public String getTechnicianPhone() {
        return technicianPhone;
    }

    public void setTechnicianPhone(String technicianPhone) {
        this.technicianPhone = technicianPhone;
    }

    @Basic
    @Column(name = "other_info")
    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @Basic
    @Column(name = "category_info")
    public String getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(String categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    @Basic
    @Column(name = "action_detail")
    public String getActionDetail() {
        return actionDetail;
    }

    public void setActionDetail(String actionDetail) {
        this.actionDetail = actionDetail;
    }

    @Basic
    @Column(name = "approval")
    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    @Basic
    @Column(name = "follow")
    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    @Basic
    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
    @Column(name = "attachments")
    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
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
        ProjectEntity that = (ProjectEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(projectTypeCode, that.projectTypeCode) && Objects.equals(customerCode, that.customerCode) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(provinceCode, that.provinceCode) && Objects.equals(districtCode, that.districtCode) && Objects.equals(addressDetail, that.addressDetail) && Objects.equals(supervisorCode, that.supervisorCode) && Objects.equals(supervisorPhone, that.supervisorPhone) && Objects.equals(technicianCode, that.technicianCode) && Objects.equals(technicianPhone, that.technicianPhone) && Objects.equals(otherInfo, that.otherInfo) && Objects.equals(categoryInfo, that.categoryInfo) && Objects.equals(actionDetail, that.actionDetail) && Objects.equals(approval, that.approval) && Objects.equals(follow, that.follow) && Objects.equals(note, that.note) && Objects.equals(status, that.status) && Objects.equals(attachments, that.attachments) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, projectTypeCode, customerCode, startDate, endDate, provinceCode, districtCode, addressDetail, supervisorCode, supervisorPhone, technicianCode, technicianPhone, otherInfo, categoryInfo, actionDetail, approval, follow, note, status, attachments, createdDate, updatedDate, createdBy, updatedBy);
    }
}
