package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.DateTimeFormatUtil;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ObjectEntity;
import com.vworks.wms.warehouse_service.models.request.object.PostDetailObjectReqBody;
import com.vworks.wms.warehouse_service.models.request.object.PostListObjectReqBody;
import com.vworks.wms.warehouse_service.models.request.object.PostUpdateObjectReqBody;
import com.vworks.wms.warehouse_service.models.response.object.PostDetailObjectResBody;
import com.vworks.wms.warehouse_service.models.response.object.PostListObjectResBody;
import com.vworks.wms.warehouse_service.repository.ObjectRepository;
import com.vworks.wms.warehouse_service.service.PartnerObjectService;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerObjectServiceImpl implements PartnerObjectService {
    private final ObjectRepository objectRepository;
    private final ServiceUtils serviceUtils;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostListObjectResBody> postListObject(PostListObjectReqBody reqBody, HttpServletRequest httpServletRequest) {
        log.info("{} postListObject requestBody {}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by(Commons.FIELD_CREATED_DATE).descending());

        Page<ObjectEntity> page = objectRepository.findAll(objectEntitySpecification(reqBody), pageable);

        List<PostListObjectResBody> list = page.getContent().stream().map(e -> {
//                modelMapper.map(e, PostListObjectResBody.class)
            return PostListObjectResBody.builder()
                    .type(e.getType())
                    .code(e.getCode())
                    .name(e.getName())
                    .addressDetail(e.getAddressDetail())
                    .agentLevelCode(e.getAgentLevelCode())
                    .districtCode(e.getDistrictCode())
                    .phoneNumber(e.getPhoneNumber())
                    .provinceCode(e.getProvinceCode())
                    .status(e.getStatus())
                    .createdDate(serviceUtils.convertTimeStampToStringWithFormatDate(e.getCreatedDate(), DateTimeFormatUtil.DD_MM_YYYY_1.getValue()))
                    .build();
        }).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postCreateObject(PostUpdateObjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateObject reqBody {}", getClass().getSimpleName(), reqBody);

        boolean existByCode = objectRepository.existsByCode(reqBody.getCode());
        if (existByCode) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        ObjectEntity objectEntity = new ObjectEntity();
        modelMapper.map(reqBody, objectEntity);
        objectEntity.setId(UUID.randomUUID().toString());
        objectEntity.setCreatedBy(serviceUtils.getUserHeader(httpServletRequest));
        objectEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        objectRepository.save(objectEntity);
        return StatusUtil.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postUpdateObject(PostUpdateObjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateObject reqBody {}", getClass().getSimpleName(), reqBody);
        ObjectEntity objectEntity = objectRepository.findByCodeOrName(reqBody.getCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        modelMapper.map(reqBody, objectEntity);
        objectEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        objectEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        objectRepository.save(objectEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public PostDetailObjectResBody postDetailObject(PostDetailObjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDetailObject reqBody {}", getClass().getSimpleName(), reqBody);
        ObjectEntity objectEntity = objectRepository.findByCodeOrName(reqBody.getCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        return modelMapper.map(objectEntity, PostDetailObjectResBody.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postDeleteObject(PostDetailObjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteObject reqBody {}", getClass().getSimpleName(), reqBody);
        ObjectEntity objectEntity = objectRepository.findByCodeOrName(reqBody.getCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (objectEntity.getStatus().equals(StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        objectEntity.setStatus(StatusUtil.DELETED.name());
        objectRepository.save(objectEntity);
        return StatusUtil.SUCCESS;
    }

    private Specification<ObjectEntity> objectEntitySpecification(PostListObjectReqBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotEmpty(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get(Commons.FIELD_STATUS), request.getStatus()));
            }
            if (StringUtils.isNotEmpty(request.getType())) {
                predicates.add(criteriaBuilder.like(root.get(Commons.FIELD_TYPE), "%" + request.getType() + "%"));
            }
            String valueSearchText = "%" + request.getSearchText() + "%";
            if (StringUtils.isNotEmpty(request.getSearchText())) {
                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), valueSearchText.toLowerCase());
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), valueSearchText.toLowerCase());
                predicates.add(criteriaBuilder.or(code, name));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
