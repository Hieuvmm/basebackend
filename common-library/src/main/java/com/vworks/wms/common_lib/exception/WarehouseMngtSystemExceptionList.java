package com.vworks.wms.common_lib.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseMngtSystemExceptionList extends Exception {
    private String errorCode;
    private String message;
    private List<ObjectError> errors;
}
