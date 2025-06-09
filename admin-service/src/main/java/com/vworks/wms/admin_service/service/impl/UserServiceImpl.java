package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.UserEntity;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.model.UserInfoDetail;
import com.vworks.wms.admin_service.model.UserJobInfo;
import com.vworks.wms.admin_service.model.UserPersonalInfo;
import com.vworks.wms.admin_service.model.requestBody.*;
import com.vworks.wms.admin_service.model.responseBody.GetByUsernameResponseBody;
import com.vworks.wms.admin_service.model.responseBody.PostGetUserByRoleResponseBody;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.admin_service.repository.UserRepository;
import com.vworks.wms.admin_service.service.UserService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.admin_service.utils.Constants;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.model.idm.request.IdmCreateUserRequest;
import com.vworks.wms.common_lib.model.idm.request.IdmUpdateUserRequest;
import com.vworks.wms.common_lib.model.request.IdmUpdateUserAttributeRequest;
import com.vworks.wms.common_lib.service.IdmService;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CommonLibConfigProperties commonLibConfigProperties;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceUtils serviceUtils;
    private final IdmService idmService;
    private final ModelMapper modelMapper;

    @Override
    public Object postCreateUser(PostCreateUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateUser with requestBody {} ", getClass().getSimpleName(), requestBody);

        if (Objects.isNull(requestBody.getUserPersonalInfo())
                || StringUtils.isEmpty(requestBody.getUserPersonalInfo().getFullName())
                || StringUtils.isEmpty(requestBody.getUserPersonalInfo().getEmail())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        if (Boolean.TRUE.equals(userRepository.existsByUserIdIsOrUsernameIs(requestBody.getUserId(), requestBody.getUsername()))) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_EXISTED.getCode(), ASExceptionTemplate.DATA_EXISTED.getMessage());
        }

        if (Boolean.TRUE.equals(userInfoRepository.existsByUserIdIsOrUsernameIsOrEmailIs(requestBody.getUserId(), requestBody.getUsername(), requestBody.getUserPersonalInfo().getEmail()))) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_EXISTED.getCode(), ASExceptionTemplate.DATA_EXISTED.getMessage());
        }

        String userCode = generateAndCheckUserCode(serviceUtils.generateCodeFromName(requestBody.getUserPersonalInfo().getFullName()));
        String createdBy = httpServletRequest.getHeader(Commons.FIELD_USER_CODE);
        Timestamp createdDate = new Timestamp(System.currentTimeMillis());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setUserId(requestBody.getUserId());
        userEntity.setUserCode(userCode);
        userEntity.setUsername(requestBody.getUsername());
        userEntity.setPassword(passwordEncoder.encode(requestBody.getPassword()));
        userEntity.setChangedPass(Boolean.FALSE);
        userEntity.setStatus(requestBody.getStatus());
        userEntity.setCreatedDate(createdDate);
        userEntity.setCreatedBy(createdBy);

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        modelMapper.map(userEntity, userInfoEntity);
        modelMapper.map(requestBody.getUserPersonalInfo(), userInfoEntity);

        if (Objects.nonNull(requestBody.getUserJobInfo())) {
            userInfoEntity.setJobDepartmentCode(requestBody.getUserJobInfo().getJobDepartmentCode());
            userInfoEntity.setJobPositionCode(requestBody.getUserJobInfo().getJobPositionCode());
            userInfoEntity.setJobTitleCode(requestBody.getUserJobInfo().getJobTitleCode());
        }

        userInfoEntity.setId(UUID.randomUUID().toString());
        userEntity.setUserInfoId(userInfoEntity.getId());

        log.info("{} postCreateUser with userEntity {},  userInfoEntity {}", getClass().getSimpleName(), new Gson().toJson(userEntity), new Gson().toJson(userInfoEntity));

        if (Boolean.TRUE.equals(commonLibConfigProperties.getKeycloak().getEnable())) {
            Integer createStatus = idmService.handleToCreateUser(IdmCreateUserRequest.builder()
                    .username(userEntity.getUsername())
                    .password(requestBody.getPassword())
                    .userCode(userEntity.getUserCode())
                    .userId(userEntity.getUserId())
                    .fullName(userInfoEntity.getFullName())
                    .email(userInfoEntity.getEmail())
                    .build());
            if (HttpStatus.CREATED.value() != createStatus) {
                return new BaseResponse<>(StatusUtil.FAILED.name(), createStatus, ASExceptionTemplate.DATA_INVALID.getCode(), ASExceptionTemplate.DATA_INVALID.getMessage());
            }
        }

        userRepository.save(userEntity);
        userInfoRepository.save(userInfoEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Object postUpdateUser(PostUpdateUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateUser with requestBody {} ", getClass().getSimpleName(), requestBody);
        if (Objects.isNull(requestBody.getUserPersonalInfo())
                || StringUtils.isEmpty(requestBody.getUserPersonalInfo().getFullName())
                || StringUtils.isEmpty(requestBody.getUserPersonalInfo().getEmail())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        Optional<UserEntity> userEntityOptional = userRepository.findFirstByUsernameOrUserCodeOrUserId(null, requestBody.getUserCode(), null);
        if (userEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        String updatedBy = httpServletRequest.getHeader(Commons.FIELD_USER_CODE);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        UserEntity userEntity = userEntityOptional.get();
        modelMapper.map(requestBody, userEntity);
        userEntity.setUpdatedDate(currentTime);
        userEntity.setUpdatedBy(updatedBy);

        Optional<UserInfoEntity> userInfoEntityOptional = userInfoRepository.findFirstByUserCodeOrUserId(userEntity.getUserCode(), null);
        if (userInfoEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        if (Objects.nonNull(requestBody.getUserPersonalInfo())) {
            modelMapper.map(requestBody.getUserPersonalInfo(), userInfoEntity);
        }

        if (Objects.nonNull(requestBody.getUserJobInfo())) {
            userInfoEntity.setJobDepartmentCode(requestBody.getUserJobInfo().getJobDepartmentCode());
            userInfoEntity.setJobPositionCode(requestBody.getUserJobInfo().getJobPositionCode());
            userInfoEntity.setJobTitleCode(requestBody.getUserJobInfo().getJobTitleCode());
            userInfoEntity.setJobOfficialDate(requestBody.getUserJobInfo().getJobOfficialDate());
            userInfoEntity.setJobOnboardDate(requestBody.getUserJobInfo().getJobOnboardDate());
            userInfoEntity.setJobManager(requestBody.getUserJobInfo().getJobManager());
            userInfoEntity.setJobAddress(requestBody.getUserJobInfo().getJobAddress());
            userInfoEntity.setJobAttendanceCode(requestBody.getUserJobInfo().getJobAttendanceCode());
        }
        userInfoEntity.setStatus(requestBody.getStatus());
        userInfoEntity.setUpdatedBy(updatedBy);
        userInfoEntity.setUpdatedDate(currentTime);
        log.info("{} postUpdateUser with userEntity {},  userInfoEntity {}", getClass().getSimpleName(), new Gson().toJson(userEntity), new Gson().toJson(userInfoEntity));

        if (Boolean.TRUE.equals(commonLibConfigProperties.getKeycloak().getEnable())) {
            Integer updateStatus = idmService.handleToUpdateUser(IdmUpdateUserRequest.builder().username(userEntity.getUsername()).status(userEntity.getStatus()).build());
            if (HttpStatus.OK.value() != updateStatus) {
                return new BaseResponse<>(StatusUtil.FAILED.name(), updateStatus, ASExceptionTemplate.DATA_INVALID.getCode(), ASExceptionTemplate.DATA_INVALID.getMessage());
            }
        }
        userRepository.save(userEntity);
        userInfoRepository.save(userInfoEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Page<UserInfoDetail> postSearchUser(PostSearchUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postSearchUser with requestBody {} ", getClass().getSimpleName(), requestBody);
        Pageable pageable = serviceUtils.handlePageable(requestBody.getPage(), requestBody.getLimit(), requestBody.getOrders());

        Specification<UserInfoEntity> userInfoEntitySpecification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate statusPredicate;
            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                statusPredicate = builder.equal(root.get(Commons.FIELD_STATUS), requestBody.getStatus());
            } else {
                statusPredicate = builder.equal(root.get(Commons.FIELD_STATUS), StatusUtil.ACTIVE.name());
            }
            predicates.add(statusPredicate);
            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                String searchValue = "%" + requestBody.getSearchText() + "%";
                Predicate userCodePredicate = builder.like(builder.lower(root.get(Commons.FIELD_USER_CODE)), searchValue);
                Predicate userIdPredicate = builder.like(builder.lower(root.get(Commons.FIELD_USER_ID)), searchValue);
                Predicate usernamePredicate = builder.like(builder.lower(root.get(Commons.FIELD_USER_NAME)), searchValue.toLowerCase());
                Predicate fullNamePredicate = builder.like(builder.lower(root.get(Commons.FIELD_FULL_NAME)), searchValue.toLowerCase());
                Predicate emailPredicate = builder.like(builder.lower(root.get(Commons.FIELD_EMAIL)), searchValue);
                predicates.add(builder.or(userCodePredicate, userIdPredicate, usernamePredicate, fullNamePredicate, emailPredicate));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Page<UserInfoEntity> userInfoEntityPage = userInfoRepository.findAll(userInfoEntitySpecification, pageable);

        if (CollectionUtils.isEmpty(userInfoEntityPage.getContent())) {
            return new PageImpl<>(new ArrayList<>(), pageable, userInfoEntityPage.getTotalElements());
        }

        List<UserInfoEntity> userInfoEntities = userInfoEntityPage.getContent();
        List<String> userInfoIds = userInfoEntities.stream().map(UserInfoEntity::getId).toList();
        List<UserEntity> userEntities = userRepository.findAllByUserInfoIdIn(userInfoIds, pageable);

        if (CollectionUtils.isEmpty(userEntities) || userEntities.size() != userInfoEntities.size()) {
            throw new WarehouseMngtSystemException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ASExceptionTemplate.DATA_INVALID.getCode(), ASExceptionTemplate.DATA_INVALID.getMessage());
        }

        List<UserInfoDetail> userInfoDetails = userEntities.stream().map(e -> {
            UserInfoDetail userInfoDetail = new UserInfoDetail();
            modelMapper.map(e, userInfoDetail);
            UserInfoEntity userInfoEntity = userInfoEntities.stream().filter(entity -> e.getUserInfoId().equals(entity.getId())).findFirst().orElseThrow();
            UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
            modelMapper.map(userInfoEntity, userPersonalInfo);
            UserJobInfo userJobInfo = new UserJobInfo();
            modelMapper.map(userInfoEntity, userJobInfo);
            userInfoDetail.setUserPersonalInfo(userPersonalInfo);
            userInfoDetail.setUserJobInfo(userJobInfo);
            return userInfoDetail;
        }).toList();
        return new PageImpl<>(userInfoDetails, pageable, userInfoEntityPage.getTotalElements());
    }

    @Override
    public Object postDeleteUser(PostDeleteUserRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteUser with requestBody {} ", getClass().getSimpleName(), requestBody);
        Optional<UserEntity> userEntityOptional = userRepository.findFirstByUsernameOrUserCodeOrUserId(requestBody.getUserName(), requestBody.getUserCode(), requestBody.getUserId());
        if (userEntityOptional.isEmpty()) {
            log.error("{} postDeleteUser User isn't exist! ", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        String updatedBy = httpServletRequest.getHeader(Commons.FIELD_USER_CODE);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        UserEntity userEntity = userEntityOptional.get();
        userEntity.setStatus(StatusUtil.DELETED.name());
        userEntity.setUpdatedDate(currentTime);
        userEntity.setUpdatedBy(updatedBy);

        Optional<UserInfoEntity> userInfoEntityOptional = userInfoRepository.findById(userEntity.getUserInfoId());
        if (userInfoEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        userInfoEntity.setStatus(StatusUtil.DELETED.name());
        userInfoEntity.setUpdatedDate(currentTime);
        userInfoEntity.setUpdatedBy(updatedBy);

        log.info("{} postUpdateUser with userEntity {},  userInfoEntity {}", getClass().getSimpleName(), new Gson().toJson(userEntity), new Gson().toJson(userInfoEntity));
        userRepository.save(userEntity);
        userInfoRepository.save(userInfoEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Object postUpdatePass(PostUpdatePassRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdatePass for user = {}, action = {} ", getClass().getSimpleName(), requestBody, requestBody.getAction());

        if (StringUtils.equals(requestBody.getAction(), Constants.TYPE_ACTION_CHANGE)
                && (StringUtils.isEmpty(requestBody.getOldPass())) || StringUtils.isEmpty(requestBody.getNewPass())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        Optional<UserEntity> userEntityOptional = userRepository.findFirstByUsernameOrUserCodeOrUserId(null, requestBody.getUserCode(), null);
        if (userEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        UserEntity userEntity = userEntityOptional.get();
        if (StringUtils.equals(requestBody.getAction(), Constants.TYPE_ACTION_CHANGE) && !passwordEncoder.matches(requestBody.getOldPass(), userEntity.getPassword())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.PASSWORD_INVALID.getCode(), ASExceptionTemplate.PASSWORD_INVALID.getMessage());
        }

        String newPass = StringUtils.equals(requestBody.getAction(), Constants.TYPE_ACTION_RESET) ? RandomStringUtils.randomAlphanumeric(8) : requestBody.getNewPass();
        userEntity.setPassword(passwordEncoder.encode(newPass));
        userEntity.setChangedPass(Boolean.TRUE);
        userEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        userEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));

        if (Boolean.TRUE.equals(commonLibConfigProperties.getKeycloak().getEnable())) {
            Integer updateStatus = idmService.handleToUpdateUser(IdmUpdateUserRequest.builder().password(newPass).username(userEntity.getUsername()).build());
            if (HttpStatus.OK.value() != updateStatus) {
                return new BaseResponse<>(StatusUtil.FAILED.name(), updateStatus, ASExceptionTemplate.DATA_INVALID.getCode(), ASExceptionTemplate.DATA_INVALID.getMessage());
            }
        }

        userRepository.save(userEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public GetByUsernameResponseBody getUserByUsername(String username) throws WarehouseMngtSystemException {
        log.info("[START] {} getUserByUsername with request username = {}",
                this.getClass().getSimpleName(), username);
        GetByUsernameResponseBody responseBody = new GetByUsernameResponseBody();
        if (StringUtils.isEmpty(username)) {
            log.error("{} getUserByUsername with username is null", this.getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.USER_INVALID.getCode(), ASExceptionTemplate.USER_INVALID.getMessage());
        }
        Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findFirstByUsername(username);

        if (userInfoEntity.isPresent()) {
            responseBody.setUserId(userInfoEntity.get().getUserId());
            responseBody.setUserCode(userInfoEntity.get().getUserCode());
            responseBody.setUserName(userInfoEntity.get().getUsername());
            responseBody.setFullName(userInfoEntity.get().getFullName());
        }

        return responseBody;
    }

    private String generateAndCheckUserCode(String userCode) {
        int countContainUserCode = userRepository.countAllByUserCodeStartsWith(userCode);

        if (countContainUserCode == 0) {
            return userCode;
        }

        return userCode + countContainUserCode;
    }

    @Override
    public List<PostGetUserByRoleResponseBody> getUserByRole(PostGetUserByRoleRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} getUserByRole with request body = {}", this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        List<UserEntity> userEntities = userRepository.findAllByStatus(Constants.STATUS_ACTIVE);
        if (requestBody.getRole() != null && StringUtils.isNotEmpty(requestBody.getRole())) {
            userEntities = userRepository.findAllByStatusAndRoleCode(Constants.STATUS_ACTIVE, requestBody.getRole());
        }

        if (userEntities.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.USER_NOT_FOUND.getCode(), ASExceptionTemplate.USER_NOT_FOUND.getMessage());
        }

        return userEntities.stream().map(x -> PostGetUserByRoleResponseBody.builder()
                .userId(x.getUserId())
                .username(x.getUsername())
                .userCode(x.getUserCode())
                .fullName(userInfoRepository.findFirstByUsername(x.getUsername()).map(UserInfoEntity::getFullName).orElse(null))
                .build()).toList();
    }

    @Override
    public Object postUpdateUserAttributes(PostUpdateUserAttributeRequest request) throws WarehouseMngtSystemException {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getAttributes())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        return idmService.updateUserAttributes(IdmUpdateUserAttributeRequest.builder().attributes(request.getAttributes()).build());
    }
}
