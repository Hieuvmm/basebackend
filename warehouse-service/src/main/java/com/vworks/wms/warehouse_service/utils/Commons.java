package com.vworks.wms.warehouse_service.utils;

public interface Commons {
    String STATUS_REGEXP = "^(" + "ACTIVE" + "|" + "INACTIVE" + "|" + "DELETED" + ")$";
    String OBJ_TYPE_REGEXP = "^(" + "CUSTOMER" + "|" + "PROVIDER" + "|" + "CUSTOMER,PROVIDER" + ")$";
    String STATUS_BILL_REGEXP = "^(" + "NEW" + "|" + "REVIEWING" + "|" + "CANCELED" + "|" + "REFUSED" + "|" + "DELETED" + ")$";
    String STATUS_APPROVE_PROJECT_REGEXP = "^(" + "APPROVED" + "|" + "GO_ON" + "|" + "HANDLING" + "|" + "REFUSED" + "|" + "CANCELED" + "|" + "PAUSED" + "|" + "DONE" + ")$";
    String USER_CODE_FIELD = "userCode";
    String CREATED_DATE_FIELD = "createdDate";
    String APPROVAL = "APPROVAL";
    String REJECT = "REJECT";
    String TYPE_EX = "EX";
    String INACTIVE = "INACTIVE";
    String DELETED = "DELETED";
}
