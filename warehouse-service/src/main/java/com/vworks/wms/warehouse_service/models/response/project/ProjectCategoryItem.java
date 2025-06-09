package com.vworks.wms.warehouse_service.models.response.project;

public class ProjectCategoryItem {
    private String status;
    private String projectCategoryCode;
    private int projectCategoryQuantity;
    private String materialCode;
    private int materialQuantity;
    private String startDate;
    private String endDate;
    private String technicianCode;
    private String note;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectCategoryCode() {
        return projectCategoryCode;
    }

    public void setProjectCategoryCode(String projectCategoryCode) {
        this.projectCategoryCode = projectCategoryCode;
    }

    public int getProjectCategoryQuantity() {
        return projectCategoryQuantity;
    }

    public void setProjectCategoryQuantity(int projectCategoryQuantity) {
        this.projectCategoryQuantity = projectCategoryQuantity;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(int materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTechnicianCode() {
        return technicianCode;
    }

    public void setTechnicianCode(String technicianCode) {
        this.technicianCode = technicianCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
