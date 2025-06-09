package com.vworks.wms.warehouse_service.controller;

import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemExceptionList;
import com.vworks.wms.warehouse_service.config.WhsConstant;
import com.vworks.wms.warehouse_service.models.request.exchangeRate.*;
import com.vworks.wms.warehouse_service.service.ExchangeRateService;
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
@RequestMapping(WhsConstant.RequestMapping.WHS_WH_MATERIAL_RATE)
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @PostMapping("/list")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'list', this)")
    public BaseResponse<?> postListExchangeRate(@Valid @RequestBody PostListExchangeRateRequest requestBody) {

        return new BaseResponse<>(exchangeRateService.postListExchangeRate(requestBody));
    }

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<?> postCreateExchangeRate(@Valid @RequestBody PostCreateExchangeRateRequest requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(exchangeRateService.postCreateExchangeRate(requestBody, httpServletRequest));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<?> postUpdateExchangeRate(@Valid @RequestBody PostUpdateExchangeRateRequest requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(exchangeRateService.postUpdateExchangeRate(requestBody, httpServletRequest));
    }

    @PostMapping("/detail")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'detail', this)")
    public BaseResponse<?> postDetailExchangeRate(@Valid @RequestBody PostDetailExchangeRateRequest requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(exchangeRateService.postDetailExchangeRate(requestBody, httpServletRequest));
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<?> postDeleteExchangeRate(@Valid @RequestBody PostDeleteExchangeRateRequest requestBody, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemExceptionList, WarehouseMngtSystemException {
        if (bindingResult.hasErrors()) {
            throw new WarehouseMngtSystemExceptionList(ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.BAD_REQUEST.getMessage(), bindingResult.getAllErrors());
        }
        return new BaseResponse<>(exchangeRateService.postDeleteExchangeRate(requestBody, httpServletRequest));

    }
}
