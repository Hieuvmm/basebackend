package com.vworks.wms.admin_service.utils;

public enum ASExceptionTemplate {
    UNAUTHORIZED("ADSYS_001", "UNAUTHORIZED!"),
    USER_DISABLED("ADSYS_001", "USER_DISABLED!"),
    INVALID_CREDENTIALS("ADSYS_001", "INVALID_CREDENTIALS!"),
    LOGIN_FAILED("ADSYS_001", "LOGIN_FAILED!"),
    REFRESH_TOKEN_FAILED("ADSYS_001", "REFRESH_TOKEN_FAILED!"),

    BAD_REQUEST("ADSYS_002", "BAD_REQUEST!"),
    REQUEST_INVALID("ADSYS_003", "Dữ liệu đầu vào không hợp lệ!"),
    DATA_EXISTED("ADSYS_004", "Dữ liệu đã tồn tại!"),
    DATA_NOT_FOUND("ADSYS_005", "Dữ liệu không tồn tại!"),
    DATA_INVALID("ADSYS_006", "Dữ liệu không hợp lệ!"),
    ERROR_REQUEST_LIST("ADSYS_007", "Danh sách lỗi đầu vào!"),
    USER_INVALID("ADSYS_008", "Người dùng không hợp lệ!"),
    USER_NOT_FOUND("ADSYS_009", "Không tìm thấy người dùng %s"),
    PASSWORD_INVALID("ADSYS_010", "Mật khẩu không hợp lệ"),
    STATUS_INVALID("ADSYS_011", "Trạng thái không hợp lệ"),
    JP_CODE_EMPTY("ADSYS_012", "Thiếu mã chức vụ/chức danh"),
    JP_CODE_INVALID("ADSYS_013", "Mã chức vụ/chức danh đã tồn tại"),
    JP_NOT_FOUND("ADSYS_014", "Không tìm thấy chức vụ/chức danh tương ứng");

    private final String code;
    private final String message;

    ASExceptionTemplate(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
