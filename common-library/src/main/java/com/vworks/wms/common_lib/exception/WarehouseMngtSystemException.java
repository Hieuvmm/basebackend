package com.vworks.wms.common_lib.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseMngtSystemException extends Exception {
    private Integer statusCode;
    private String errorCode;
    private String message;
}
