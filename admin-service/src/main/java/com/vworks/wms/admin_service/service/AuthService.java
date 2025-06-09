package com.vworks.wms.admin_service.service;

import com.vworks.wms.admin_service.model.requestBody.PostLoginRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostRefreshTokenRequestBody;
import com.vworks.wms.admin_service.model.responseBody.PostLoginResponseBody;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    PostLoginResponseBody postLogin(PostLoginRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostLoginResponseBody postRefreshToken(PostRefreshTokenRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Boolean postLogout(HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

//    Object postFetchAccount(PostFetchAccountRequestBody requestBody, HttpServletRequest httpServletRequest);
}
