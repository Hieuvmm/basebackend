package com.vworks.wms.admin_service.controller;

import com.vworks.wms.admin_service.config.AsConstant;
import com.vworks.wms.admin_service.model.requestBody.PostLoginRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostRefreshTokenRequestBody;
import com.vworks.wms.admin_service.service.AuthService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemExceptionList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AsConstant.RequestMapping.AS_AUTH)
@CrossOrigin("*")
public class AuthController {
    private final AuthService authService;

    @PostMapping(AsConstant.Endpoint.LOGIN)
    public BaseResponse<Object> postLogin(@RequestBody @Valid PostLoginRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(authService.postLogin(requestBody, httpServletRequest));
    }

    @PostMapping(AsConstant.Endpoint.REFRESH)
    public BaseResponse<Object> postRefreshToken(@RequestBody @Valid PostRefreshTokenRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(authService.postRefreshToken(requestBody, httpServletRequest));
    }

    @PostMapping(AsConstant.Endpoint.LOGOUT)
    public BaseResponse<Object> postLogout(HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(authService.postLogout(httpServletRequest));
    }
}
