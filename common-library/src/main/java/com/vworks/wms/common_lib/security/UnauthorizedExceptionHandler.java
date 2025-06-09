package com.vworks.wms.common_lib.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.utils.ExceptionTemplate;
import com.vworks.wms.common_lib.utils.StatusUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("unauthorizedExceptionHandler")
public class UnauthorizedExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        BaseResponse<?> apiResponse = BaseResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode(ExceptionTemplate.UNAUTHORIZED.getCode())
                .message(ExceptionTemplate.UNAUTHORIZED.getMessage())
                .body(StatusUtil.FAILED.name())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
