package com.vworks.wms.admin_service.controller;

import com.vworks.wms.admin_service.config.AsConstant;
import com.vworks.wms.admin_service.model.requestBody.PostCreateDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateDepartmentRequestBody;
import com.vworks.wms.admin_service.service.DepartmentService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemExceptionList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AsConstant.RequestMapping.AS_ORG_DEPARTMENT)
@CrossOrigin("*")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<Object> postCreateDepartment(@RequestBody @Valid PostCreateDepartmentRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(departmentService.postCreateDepartment(requestBody, httpServletRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<Object> postUpdateDepartment(@RequestBody @Valid PostUpdateDepartmentRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(departmentService.postUpdateDepartment(requestBody, httpServletRequest));
    }

    @PostMapping("/search")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'search', this)")
    public BaseResponse<?> postSearchDepartment(@RequestBody PostSearchDepartmentRequestBody requestBody, HttpServletRequest httpServletRequest) {
        return new BaseResponse<>(departmentService.postSearchDepartment(requestBody, httpServletRequest));
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<Object> postDeleteDepartment(@RequestBody @Valid PostDeleteDepartmentRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(departmentService.postDeleteDepartment(requestBody, httpServletRequest));
    }
}
