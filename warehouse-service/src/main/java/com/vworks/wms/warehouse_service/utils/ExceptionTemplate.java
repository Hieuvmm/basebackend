package com.vworks.wms.warehouse_service.utils;

public enum ExceptionTemplate {
    WH_CODE_EXIST("WH-01", "Mã kho đã tồn tại"),
    INPUT_EMPTY("WH-02", "Thiếu thông tin đầu vào"),
    WH_CODE_NOT_FOUND("WH-03", "Không tìm thấy kho"),
    EX_CODE_EXIST("WH-04", "Mã xuất kho đã tồn tại"),
    EX_CODE_NOT_FOUND("WH-08", "Không tìm thấy mã phiếu xuất tương ứng"),
    MATERIAL_CODE_EMPTY("WH-05", "Mã vật tư trống"),
    QUANTITY_EMPTY("WH-06", "Thiếu số lượng trong phiếu xuất"),
    UNAUTHORIZED("WH_001", "UNAUTHORIZED!"),
    USER_DISABLED("WH_001", "USER_DISABLED!"),
    INVALID_CREDENTIALS("WH_001", "INVALID_CREDENTIALS!"),
    LOGIN_FAILED("WH_001", "LOGIN_FAILED!"),
    USERNAME_NOT_FOUND("WH-07", "Không tìm thấy người dùng"),
    USER_NOT_APPROVAL("WH-09", "User không được phép duyệt/từ chối"),
    TIME_EMPTY("WH-10", "Thiếu thông tin đầu vào số lần"),
    BAD_REQUEST("WH_002", "BAD_REQUEST!"),
    REQUEST_INVALID("WH_003", "Dữ liệu đầu vào không hợp lệ!"),
    DATA_EXISTED("WH_004", "Dữ liệu tìm kiếm đã tồn tại!"),
    DATA_NOT_FOUND("WH_005", "Dữ liệu tìm kiếm không tồn tại!"),
    ERROR_REQUEST_LIST("WH_006", "Danh sách lỗi đầu vào!"),
    USER_INVALID("WH_007", "Người dùng không hợp lệ!"),
    USER_NOT_FOUND("WH_007", "Không tìm thấy người dùng %s"),
    PASSWORD_INVALID("WH_008", "Mật khẩu không hợp lệ"),
    STATUS_INVALID("WH_009", "Trạng thái không hợp lệ"),
    APPROVAL_INVALID("WH_010", "Người duyệt không hợp lệ"),
    EX_BILL_INACTIVE("WH_011", "Phiếu xuất đã bị từ chối"),
    CODE_EXIST("CODE_EXIST", "Mã đã tồn tại"),
    NAME_EXIST("NAME_EXIST", "Tên đã tồn tại"),
    OBJECT_INVALID("OBJECT_INVALID", "Đối tượng không hợp lệ"),
    APPROVAL_EMPTY("WH_012", "Chưa có thông tin người phê duyệt"),
    QUANTITY_INVALID("WH_013", "Số lượng trong kho không đủ, yêu cầu bổ sung"),
    REASON_EMPTY("WH_014", "Cần nhập lý do từ chối"),
    WH_SAME("WH_015", "mã kho trùng nhau");

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

    public String format(Object... args) {
        return String.format(message, args);
    }
}
