package com.vworks.wms.admin_service.service;

import com.vworks.wms.admin_service.model.UserInfoDetail;
import com.vworks.wms.admin_service.model.requestBody.*;
import com.vworks.wms.admin_service.model.responseBody.GetByUsernameResponseBody;
import com.vworks.wms.admin_service.model.responseBody.PostGetUserByRoleResponseBody;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Object postCreateUser(PostCreateUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateUser(PostUpdateUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Page<UserInfoDetail> postSearchUser(PostSearchUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdatePass(PostUpdatePassRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postDeleteUser(PostDeleteUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    GetByUsernameResponseBody getUserByUsername(String username) throws WarehouseMngtSystemException;

    List<PostGetUserByRoleResponseBody> getUserByRole(PostGetUserByRoleRequestBody requestBody) throws WarehouseMngtSystemException;

    Object postUpdateUserAttributes(PostUpdateUserAttributeRequest request) throws WarehouseMngtSystemException;
}
