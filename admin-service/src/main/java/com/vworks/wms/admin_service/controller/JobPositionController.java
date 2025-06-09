package com.vworks.wms.admin_service.controller;

import com.vworks.wms.admin_service.config.AsConstant;
import com.vworks.wms.admin_service.model.requestBody.PostCreateJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateJobPositionRequestBody;
import com.vworks.wms.admin_service.service.JobPositionService;
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
@RequestMapping(AsConstant.RequestMapping.AS_USER_POSITION)
@RequiredArgsConstructor
@CrossOrigin("*")
public class JobPositionController {
    private final JobPositionService jobPositionService;

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<Object> postCreateJobPosition(@RequestBody PostCreateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobPositionService.postCreateJobPosition(requestBody, httpServletRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<Object> postUpdateJobPosition(@RequestBody PostUpdateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobPositionService.postUpdateJobPosition(requestBody, httpServletRequest));
    }

    @PostMapping("/search")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'search', this)")
    public BaseResponse<?> postSearchJobPosition(@RequestBody PostSearchJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobPositionService.postSearchJobPosition(requestBody, httpServletRequest));
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<Object> postDeleteJobPosition(@RequestBody @Valid PostDeleteJobPositionRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(jobPositionService.postDeleteJobPosition(requestBody, httpServletRequest));
    }
}
