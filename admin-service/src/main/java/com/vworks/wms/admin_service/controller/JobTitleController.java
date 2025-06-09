package com.vworks.wms.admin_service.controller;

import com.vworks.wms.admin_service.config.AsConstant;
import com.vworks.wms.admin_service.model.requestBody.PostCreateJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateJobTitleRequestBody;
import com.vworks.wms.admin_service.service.JobTitleService;
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
@RequestMapping(AsConstant.RequestMapping.AS_USER_TITLE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class JobTitleController {
    private final JobTitleService jobTitleService;

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<Object> createJobTitleController(@RequestBody PostCreateJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobTitleService.postCreateJobTitle(requestBody, httpServletRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<Object> updateJobTitleController(@RequestBody PostUpdateJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobTitleService.postUpdateJobTitle(requestBody, httpServletRequest));
    }

    @PostMapping("/search")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'search', this)")
    public BaseResponse<?> searchJobTitle(@RequestBody PostSearchJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return new BaseResponse<>(jobTitleService.postSearchJobTitle(requestBody, httpServletRequest));
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<Object> postDeleteJobTitle(@RequestBody @Valid PostDeleteJobTitleRequestBody requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException, WarehouseMngtSystemExceptionList {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ASExceptionTemplate.ERROR_REQUEST_LIST.getCode(), ASExceptionTemplate.ERROR_REQUEST_LIST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(jobTitleService.postDeleteJobTitle(requestBody, httpServletRequest));
    }
}
