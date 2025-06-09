package com.vworks.wms.common_lib.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
    private int status;
    private String errorCode;
    private String message;
    private Object body;

    public BaseResponse(T data) {
        this.status = HttpStatus.OK.value();
        this.errorCode = HttpStatus.Series.SUCCESSFUL.name();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.body = data;
    }

    public BaseResponse(T data, int statusCode, String errorCode, String message) {
        this.status = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        this.body = data;
    }

    public BaseResponse(List<T> data) {
        this.status = HttpStatus.OK.value();
        this.errorCode = HttpStatus.Series.SUCCESSFUL.name();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.body = data;
    }

    public BaseResponse(Page<T> data) {
        this.status = HttpStatus.OK.value();
        this.errorCode = HttpStatus.Series.SUCCESSFUL.name();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.body = new BasePagination<T>(data);
    }

}
