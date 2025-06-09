package com.vworks.wms.admin_service.entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_info", schema = "admin-service", catalog = "db-cuongphong-warehouse-mngt")
public class UserInfoEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "user_id")
    private String userId;
    @Basic
    @Column(name = "user_code")
    private String userCode;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "full_name")
    private String fullName;
    @Basic
    @Column(name = "birthday")
    private Date birthday;
    @Basic
    @Column(name = "gender")
    private String gender;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "personal_email")
    private String personalEmail;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "avatar")
    private String avatar;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "job_attendance_code")
    private String jobAttendanceCode;
    @Basic
    @Column(name = "job_department_code")
    private String jobDepartmentCode;
    @Basic
    @Column(name = "job_manager")
    private String jobManager;
    @Basic
    @Column(name = "job_title_code")
    private String jobTitleCode;
    @Basic
    @Column(name = "job_position_code")
    private String jobPositionCode;
    @Basic
    @Column(name = "job_address")
    private String jobAddress;
    @Basic
    @Column(name = "job_onboard_date")
    private Date jobOnboardDate;
    @Basic
    @Column(name = "job_official_date")
    private Date jobOfficialDate;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getJobAttendanceCode() {
        return jobAttendanceCode;
    }

    public void setJobAttendanceCode(String jobAttendanceCode) {
        this.jobAttendanceCode = jobAttendanceCode;
    }

    public String getJobDepartmentCode() {
        return jobDepartmentCode;
    }

    public void setJobDepartmentCode(String jobDepartmentCode) {
        this.jobDepartmentCode = jobDepartmentCode;
    }

    public String getJobManager() {
        return jobManager;
    }

    public void setJobManager(String jobManager) {
        this.jobManager = jobManager;
    }

    public String getJobTitleCode() {
        return jobTitleCode;
    }

    public void setJobTitleCode(String jobTitleCode) {
        this.jobTitleCode = jobTitleCode;
    }

    public String getJobPositionCode() {
        return jobPositionCode;
    }

    public void setJobPositionCode(String jobPositionCode) {
        this.jobPositionCode = jobPositionCode;
    }

    public String getJobAddress() {
        return jobAddress;
    }

    public void setJobAddress(String jobAddress) {
        this.jobAddress = jobAddress;
    }

    public Date getJobOnboardDate() {
        return jobOnboardDate;
    }

    public void setJobOnboardDate(Date jobOnboardDate) {
        this.jobOnboardDate = jobOnboardDate;
    }

    public Date getJobOfficialDate() {
        return jobOfficialDate;
    }

    public void setJobOfficialDate(Date jobOfficialDate) {
        this.jobOfficialDate = jobOfficialDate;
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
        UserInfoEntity that = (UserInfoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(userCode, that.userCode) && Objects.equals(username, that.username) && Objects.equals(fullName, that.fullName) && Objects.equals(birthday, that.birthday) && Objects.equals(gender, that.gender) && Objects.equals(phone, that.phone) && Objects.equals(email, that.email) && Objects.equals(personalEmail, that.personalEmail) && Objects.equals(address, that.address) && Objects.equals(avatar, that.avatar) && Objects.equals(status, that.status) && Objects.equals(jobAttendanceCode, that.jobAttendanceCode) && Objects.equals(jobDepartmentCode, that.jobDepartmentCode) && Objects.equals(jobManager, that.jobManager) && Objects.equals(jobTitleCode, that.jobTitleCode) && Objects.equals(jobPositionCode, that.jobPositionCode) && Objects.equals(jobAddress, that.jobAddress) && Objects.equals(jobOnboardDate, that.jobOnboardDate) && Objects.equals(jobOfficialDate, that.jobOfficialDate) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, userCode, username, fullName, birthday, gender, phone, email, personalEmail, address, avatar, status, jobAttendanceCode, jobDepartmentCode, jobManager, jobTitleCode, jobPositionCode, jobAddress, jobOnboardDate, jobOfficialDate, createdDate, updatedDate, createdBy, updatedBy);
    }
}
