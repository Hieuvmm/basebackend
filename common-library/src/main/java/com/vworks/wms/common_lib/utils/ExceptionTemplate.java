package com.vworks.wms.common_lib.utils;

public enum ExceptionTemplate {
    UNAUTHORIZED("401", "Unauthorized access!"),
    BAD_REQUEST("400", "Bad request!"),
    INTERNAL_SERVER_ERROR("500", "Internal server error!"),
    INVALID_TOKEN("401", "Invalid or null token provided!"),
    ACCESS_DENIED("403", "Access denied due to insufficient permissions!"),
    OBJECT_MAPPER_ERROR("400", "OBJECT_MAPPER_ERROR");

    private final String code;
    private final String message;

    ExceptionTemplate(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
