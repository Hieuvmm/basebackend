package com.vworks.wms.warehouse_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "im_ex_bill", schema = "warehouse-service", catalog = "db-cuongphong-warehouse-mngt")
public class ImExBillEntity {
    @Basic
    @Id
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "type")
    private String type;
    @Basic
    @Column(name = "code")
    private String code;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "order_code")
    private String orderCode;
    @Basic
    @Column(name = "import_date")
    private String importDate;
    @Basic
    @Column(name = "order_date")
    private String orderDate;
    @Basic
    @Column(name = "content")
    private String content;
    @Basic
    @Column(name = "provider_code")
    private String providerCode;
    @Basic
    @Column(name = "wh_code")
    private String whCode;
    @Basic
    @Column(name = "delivery_method")
    private String deliveryMethod;
    @Basic
    @Column(name = "exchange_rate_code")
    private String exchangeRateCode;
    @Basic
    @Column(name = "attachment")
    private String attachment;
    @Basic
    @Column(name = "additional_info")
    private String additionalInfo;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "trans_type")
    private String transType;
    @Basic
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Basic
    @Column(name = "approve_detail")
    private String approveDetail;
    @Basic
    @Column(name = "approved_detail")
    private String approvedDetail;
    @Basic
    @Column(name = "follow_detail")
    private String followDetail;
    @Basic
    @Column(name = "date_ex")
    private String dateEx;
    @Basic
    @Column(name = "customer")
    private String customer;
    @Basic
    @Column(name = "cancel_by")
    private String cancelBy;
    @Basic
    @Column(name = "reason")
    private String reason;
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
    @Basic
    @Column(name = "destination_wh")
    private String destinationWh;

    public String getId() {
        return id;
    }

    public String getDestinationWh() {
        return destinationWh;
    }

    public void setDestinationWh(String destinationWh) {
        this.destinationWh = destinationWh;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getExchangeRateCode() {
        return exchangeRateCode;
    }

    public void setExchangeRateCode(String exchangeRateCode) {
        this.exchangeRateCode = exchangeRateCode;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getApproveDetail() {
        return approveDetail;
    }

    public void setApproveDetail(String approveDetail) {
        this.approveDetail = approveDetail;
    }

    public String getApprovedDetail() {
        return approvedDetail;
    }

    public void setApprovedDetail(String approvedDetail) {
        this.approvedDetail = approvedDetail;
    }

    public String getFollowDetail() {
        return followDetail;
    }

    public void setFollowDetail(String followDetail) {
        this.followDetail = followDetail;
    }

    public String getDateEx() {
        return dateEx;
    }

    public void setDateEx(String dateEx) {
        this.dateEx = dateEx;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCancelBy() {
        return cancelBy;
    }

    public void setCancelBy(String cancelBy) {
        this.cancelBy = cancelBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImExBillEntity that = (ImExBillEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (destinationWh != null ? !destinationWh.equals(that.destinationWh) : that.destinationWh != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (orderCode != null ? !orderCode.equals(that.orderCode) : that.orderCode != null) return false;
        if (importDate != null ? !importDate.equals(that.importDate) : that.importDate != null) return false;
        if (orderDate != null ? !orderDate.equals(that.orderDate) : that.orderDate != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (providerCode != null ? !providerCode.equals(that.providerCode) : that.providerCode != null) return false;
        if (whCode != null ? !whCode.equals(that.whCode) : that.whCode != null) return false;
        if (deliveryMethod != null ? !deliveryMethod.equals(that.deliveryMethod) : that.deliveryMethod != null)
            return false;
        if (exchangeRateCode != null ? !exchangeRateCode.equals(that.exchangeRateCode) : that.exchangeRateCode != null)
            return false;
        if (attachment != null ? !attachment.equals(that.attachment) : that.attachment != null) return false;
        if (additionalInfo != null ? !additionalInfo.equals(that.additionalInfo) : that.additionalInfo != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (updatedDate != null ? !updatedDate.equals(that.updatedDate) : that.updatedDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (updatedBy != null ? !updatedBy.equals(that.updatedBy) : that.updatedBy != null) return false;
        if (transType != null ? !transType.equals(that.transType) : that.transType != null) return false;
        if (totalPrice != null ? !totalPrice.equals(that.totalPrice) : that.totalPrice != null) return false;
        if (approveDetail != null ? !approveDetail.equals(that.approveDetail) : that.approveDetail != null)
            return false;
        if (approvedDetail != null ? !approvedDetail.equals(that.approvedDetail) : that.approvedDetail != null)
            return false;
        if (followDetail != null ? !followDetail.equals(that.followDetail) : that.followDetail != null) return false;
        if (dateEx != null ? !dateEx.equals(that.dateEx) : that.dateEx != null) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (cancelBy != null ? !cancelBy.equals(that.cancelBy) : that.cancelBy != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (orderCode != null ? orderCode.hashCode() : 0);
        result = 31 * result + (importDate != null ? importDate.hashCode() : 0);
        result = 31 * result + (orderDate != null ? orderDate.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (providerCode != null ? providerCode.hashCode() : 0);
        result = 31 * result + (whCode != null ? whCode.hashCode() : 0);
        result = 31 * result + (deliveryMethod != null ? deliveryMethod.hashCode() : 0);
        result = 31 * result + (exchangeRateCode != null ? exchangeRateCode.hashCode() : 0);
        result = 31 * result + (attachment != null ? attachment.hashCode() : 0);
        result = 31 * result + (additionalInfo != null ? additionalInfo.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        result = 31 * result + (transType != null ? transType.hashCode() : 0);
        result = 31 * result + (totalPrice != null ? totalPrice.hashCode() : 0);
        result = 31 * result + (approveDetail != null ? approveDetail.hashCode() : 0);
        result = 31 * result + (approvedDetail != null ? approvedDetail.hashCode() : 0);
        result = 31 * result + (followDetail != null ? followDetail.hashCode() : 0);
        result = 31 * result + (dateEx != null ? dateEx.hashCode() : 0);
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (cancelBy != null ? cancelBy.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (destinationWh != null ? destinationWh.hashCode() : 0);
        return result;
    }
}
