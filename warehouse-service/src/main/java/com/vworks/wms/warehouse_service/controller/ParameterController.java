package com.vworks.wms.warehouse_service.controller;

import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemExceptionList;
import com.vworks.wms.warehouse_service.config.WhsConstant;
import com.vworks.wms.warehouse_service.models.request.parameter.PostCreateOrUpdateParameterReqBody;
import com.vworks.wms.warehouse_service.models.request.parameter.PostHandleByCodeParameterReqBody;
import com.vworks.wms.warehouse_service.models.request.parameter.PostListParameterReqBody;
import com.vworks.wms.warehouse_service.service.ParameterService;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping(WhsConstant.RequestMapping.WHS_PARAMETER)
public class ParameterController {
    private final ParameterService parameterService;

    @PostMapping("/list")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'list', this)")
    public BaseResponse<?> postListParameter(@Valid @RequestBody PostListParameterReqBody requestBody) {

        return new BaseResponse<>(parameterService.postListParameter(requestBody));
    }

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<?> postCreateParameter(@Valid @RequestBody PostCreateOrUpdateParameterReqBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(parameterService.postCreateParameter(requestBody, httpServletRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<?> postUpdateParameter(@Valid @RequestBody PostCreateOrUpdateParameterReqBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(parameterService.postUpdateParameter(requestBody, httpServletRequest));
    }

    @PostMapping("/detail")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'detail', this)")
    public BaseResponse<?> postDetailParameter(@Valid @RequestBody PostHandleByCodeParameterReqBody requestBody, BindingResult bindingResult) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(parameterService.postDetailParameter(requestBody));
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<?> postDeleteParameter(@Valid @RequestBody PostHandleByCodeParameterReqBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(parameterService.postDeleteParameter(requestBody, httpServletRequest));

    }
}
