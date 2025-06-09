package com.vworks.wms.common_lib.exception;

import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.utils.ExceptionTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class WarehouseMngtSystemExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({WarehouseMngtSystemExceptionList.class})
    public ResponseEntity<Object> handleWarehouseMngtSystemExceptionList(WarehouseMngtSystemExceptionList ex, WebRequest request) {
        log.error(" {} handleWarehouseMngtSystemExceptionList e = {} ", getClass().getSimpleName(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorDefaultMessage = String.format(Objects.requireNonNull(error.getDefaultMessage()), fieldName);
            errors.put(fieldName, errorDefaultMessage);
        });

        return new ResponseEntity<>(new BaseResponse<>(400, ex.getErrorCode(), ex.getMessage(), errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({WarehouseMngtSystemException.class})
    public ResponseEntity<Object> handleWarehouseMngtSystemException(WarehouseMngtSystemException ex, WebRequest request) {
        log.error(" {} handleWarehouseMngtSystemException e = {} ", getClass().getSimpleName(), ex);
        return new ResponseEntity<>(new BaseResponse<String>(ex.getStatusCode(), ex.getErrorCode(), ex.getMessage(), null), new HttpHeaders(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error(" {} handleAccessDeniedException e = {} ", getClass().getSimpleName(), ex);
        return new ResponseEntity<>(new BaseResponse<String>(HttpStatus.FORBIDDEN.value(), ExceptionTemplate.ACCESS_DENIED.getCode(), ex.getMessage(), null), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleWarehouseMngtSystemAllException(Exception ex, WebRequest request) {
        log.error(" {} handleWarehouseMngtSystemAllException e = {} ", getClass().getSimpleName(), ex);
        return new ResponseEntity<>(new BaseResponse<String>(500, ex.getMessage(), ex.getMessage(), null), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
