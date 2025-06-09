package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.BranchEntity;
import com.vworks.wms.admin_service.entity.DepartmentEntity;
import com.vworks.wms.admin_service.model.DepartmentInfo;
import com.vworks.wms.admin_service.model.requestBody.PostCreateBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateBranchRequestBody;
import com.vworks.wms.admin_service.model.responseBody.PostSearchBranchResponseBody;
import com.vworks.wms.admin_service.repository.BranchRepository;
import com.vworks.wms.admin_service.repository.DepartmentRepository;
import com.vworks.wms.admin_service.service.BranchService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.model.WarehouseInfo;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final ModelMapper modelMapper;
    private final ServiceUtils serviceUtils;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Object postCreateBranch(PostCreateBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateBranch requestBody {} ", getClass().getSimpleName(), requestBody);
        if (Boolean.TRUE.equals(branchRepository.existsAllByCode(requestBody.getCode()))) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_EXISTED.getCode(), ASExceptionTemplate.DATA_EXISTED.getMessage());
        }

        BranchEntity branchEntity = BranchEntity.builder()
                .id(UUID.randomUUID().toString())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .createdBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE))
                .build();

        modelMapper.map(requestBody, branchEntity);

        log.info("{} postCreateBranch branchEntity {}", getClass().getSimpleName(), new Gson().toJson(branchEntity));
        branchRepository.save(branchEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateBranch(PostUpdateBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateBranch requestBody {} ", getClass().getSimpleName(), requestBody);
        BranchEntity branchEntity = branchRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (Objects.isNull(branchEntity)) {
            log.info("{} postUpdateBranch branchEntity is null", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        modelMapper.map(requestBody, branchEntity);

        branchEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        branchEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));
        branchRepository.save(branchEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Page<PostSearchBranchResponseBody> postSearchBranch(PostSearchBranchRequestBody requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postSearchBranch requestBody {} ", getClass().getSimpleName(), requestBody);
        Pageable pageable = serviceUtils.handlePageable(requestBody.getPage(), requestBody.getLimit(), requestBody.getOrders());

        Specification<BranchEntity> branchSpecification = (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                Predicate statusPredicate = builder.equal(root.get(Commons.FIELD_STATUS), requestBody.getStatus());
                predicateList.add(statusPredicate);
            }

            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                Predicate codePredicate = builder.equal(builder.lower(root.get(Commons.FIELD_CODE)), "%" + requestBody.getSearchText().toLowerCase() + "%");
                Predicate namePredicate = builder.like(builder.lower(root.get(Commons.FIELD_NAME)), "%" + requestBody.getSearchText().toLowerCase() + "%");
                predicateList.add(builder.or(codePredicate, namePredicate));
            }

            return builder.and(predicateList.toArray(new Predicate[0]));
        };

        Page<BranchEntity> branchEntityPage = branchRepository.findAll(branchSpecification, pageable);
        if (CollectionUtils.isEmpty(branchEntityPage.getContent())) {
            return new PageImpl<>(new ArrayList<>(), pageable, branchEntityPage.getTotalElements());
        }

        List<String> departmentCodes = branchEntityPage.getContent().stream()
                .flatMap(branchEntity -> StringUtils.isNotEmpty(branchEntity.getDepartmentCode()) ? Arrays.stream(StringUtils.split(branchEntity.getDepartmentCode(), ",")) : null)
                .toList();
        List<DepartmentInfo> departmentInfos = null;
        if (!CollectionUtils.isEmpty(departmentCodes)) {
            List<DepartmentEntity> departmentEntities = departmentRepository.findAllByCodeIn(departmentCodes);
            departmentInfos = departmentEntities.stream().map(e -> {
                DepartmentInfo departmentInfo = new DepartmentInfo();
                modelMapper.map(e, departmentInfo);
                return departmentInfo;
            }).toList();
        }

        List<String> warehouseCodes = branchEntityPage.getContent().stream()
                .flatMap(branchEntity -> StringUtils.isNotEmpty(branchEntity.getWarehouseCode()) ? Arrays.stream(StringUtils.split(branchEntity.getWarehouseCode(), ",")) : null)
                .toList();
        List<WarehouseInfo> warehouseInfos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(warehouseCodes)) {
//            warehouseInfos = warehouseServiceUtils.fetchWarehouseDetailList(warehouseCodes);
            warehouseInfos = null;
        }

        List<DepartmentInfo> finalDepartmentInfos = departmentInfos;
        List<WarehouseInfo> finalWarehouseInfos = warehouseInfos;
        List<PostSearchBranchResponseBody> responseBodyList = branchEntityPage.getContent().stream().map(e -> {
            PostSearchBranchResponseBody responseBody = new PostSearchBranchResponseBody();
            modelMapper.map(e, responseBody);
            if (!CollectionUtils.isEmpty(finalDepartmentInfos)) {
                List<DepartmentInfo> list = finalDepartmentInfos.stream().filter(departmentInfo -> StringUtils.contains(e.getDepartmentCode(), departmentInfo.getCode())).toList();
                responseBody.setDepartmentInfo(CollectionUtils.isEmpty(list) ? null : list);
            }

            if (!CollectionUtils.isEmpty(finalWarehouseInfos)) {
                List<WarehouseInfo> list = finalWarehouseInfos.stream().filter(warehouseInfo -> StringUtils.contains(e.getWarehouseCode(), warehouseInfo.getCode())).toList();
                responseBody.setWarehouseInfo(CollectionUtils.isEmpty(list) ? null : list);
            }
            return responseBody;
        }).toList();

        return new PageImpl<>(responseBodyList, pageable, branchEntityPage.getTotalElements());
    }

    @Override
    public Object postDeleteBranch(PostDeleteBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteBranch with requestBody {} ", getClass().getSimpleName(), requestBody);
        BranchEntity branchEntity = branchRepository.findFirstByCodeOrId(requestBody.getCode(), null);
        if (Objects.isNull(branchEntity)) {
            log.error("{} postDeleteBranch branchEntity isn't exist! ", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        branchEntity.setStatus(StatusUtil.DELETED.name());
        branchEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        branchEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));

        branchRepository.save(branchEntity);
        return StatusUtil.SUCCESS.name();
    }
}
