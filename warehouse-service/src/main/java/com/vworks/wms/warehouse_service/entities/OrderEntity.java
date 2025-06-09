package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "order", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    private String id;
    private String code;
    private String customerCode;
    private String customerType;
    private String orderType;
    private String deliveryMethod;
    private String exchangeRateCode;
    private BigDecimal total;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
    private String approvalBy;
    private String approvedBy;
    private String followBy;
    private String cancelBy;
    private String reason;
    private String whExport;
    private Integer discountRate;
    private Integer tax;
    private Timestamp advanceDate;
    private BigDecimal advanceAmount;
    private String paidMethod;
    private String note;

    @Id
    @Basic
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
    @Column(name = "customer_code")
    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @Basic
    @Column(name = "customer_type")
    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    @Basic
    @Column(name = "order_type")
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Basic
    @Column(name = "delivery_method")
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    @Basic
    @Column(name = "exchange_rate_code")
    public String getExchangeRateCode() {
        return exchangeRateCode;
    }

    public void setExchangeRateCode(String exchangeRateCode) {
        this.exchangeRateCode = exchangeRateCode;
    }

    @Basic
    @Column(name = "total")
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    @Basic
    @Column(name = "approval_by")
    public String getApprovalBy() {
        return approvalBy;
    }

    public void setApprovalBy(String approvalBy) {
        this.approvalBy = approvalBy;
    }

    @Basic
    @Column(name = "follow_by")
    public String getFollowBy() {
        return followBy;
    }

    public void setFollowBy(String followBy) {
        this.followBy = followBy;
    }

    @Basic
    @Column(name = "approved_by")
    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    @Basic
    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Basic
    @Column(name = "cancel_by")
    public String getCancelBy() {
        return cancelBy;
    }

    public void setCancelBy(String cancelBy) {
        this.cancelBy = cancelBy;
    }

    @Basic
    @Column(name = "discount_rate")
    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    @Basic
    @Column(name = "wh_export")
    public String getWhExport() {
        return whExport;
    }

    public void setWhExport(String whExport) {
        this.whExport = whExport;
    }

    @Basic
    @Column(name = "tax")
    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    @Basic
    @Column(name = "advance_date")
    public Timestamp getAdvanceDate() {
        return advanceDate;
    }

    public void setAdvanceDate(Timestamp advanceDate) {
        this.advanceDate = advanceDate;
    }

    @Basic
    @Column(name = "paidMethod")
    public String getPaidMethod() {
        return paidMethod;
    }

    public void setPaidMethod(String paidMethod) {
        this.paidMethod = paidMethod;
    }

    @Basic
    @Column(name = "advance_amount")
    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    @Basic
    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(customerCode, that.customerCode) && Objects.equals(customerType, that.customerType) && Objects.equals(orderType, that.orderType) && Objects.equals(deliveryMethod, that.deliveryMethod) && Objects.equals(exchangeRateCode, that.exchangeRateCode) && Objects.equals(total, that.total) && Objects.equals(status, that.status) && Objects.equals(createdDate, that.createdDate)
                && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy)
                && Objects.equals(approvalBy, that.approvalBy) && Objects.equals(approvedBy, that.approvedBy) && Objects.equals(followBy, that.followBy)
                && Objects.equals(cancelBy, that.cancelBy) && Objects.equals(reason, that.reason)
                && Objects.equals(whExport, that.whExport) && Objects.equals(discountRate, that.discountRate)
                && Objects.equals(tax, that.tax) && Objects.equals(advanceDate, that.advanceDate)
                && Objects.equals(advanceAmount, that.advanceAmount) && Objects.equals(paidMethod, that.paidMethod)
                && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, customerCode, customerType, orderType,
                deliveryMethod, exchangeRateCode, total, status, createdDate,
                updatedDate, createdBy, updatedBy, approvalBy, approvedBy,
                followBy, cancelBy, reason, whExport, discountRate, tax,
                advanceDate, advanceAmount, paidMethod, note);
    }


}
