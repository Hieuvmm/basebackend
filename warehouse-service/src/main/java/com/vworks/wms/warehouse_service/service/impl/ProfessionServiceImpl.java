package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ProfessionEntity;
import com.vworks.wms.warehouse_service.models.request.profession.PostCreateOrUpdateProfessionReqBody;
import com.vworks.wms.warehouse_service.models.request.profession.PostHandleByCodeProfessionReqBody;
import com.vworks.wms.warehouse_service.models.request.profession.PostListProfessionReqBody;
import com.vworks.wms.warehouse_service.models.response.profession.PostGetProfessionResBody;
import com.vworks.wms.warehouse_service.repository.ProfessionRepository;
import com.vworks.wms.warehouse_service.service.ProfessionService;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfessionServiceImpl implements ProfessionService {
    private final ProfessionRepository professionRepo;
    private final ModelMapper modelMapper;
    private final ServiceUtils serviceUtils;

    @Override
    public Page<PostGetProfessionResBody> postListProfession(PostListProfessionReqBody reqBody) {
        log.info("{} postListProfession reqBody{}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by("createdDate").descending());

        Page<ProfessionEntity> page = professionRepo.findAll(professionSpec(reqBody), pageable);

        List<PostGetProfessionResBody> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostGetProfessionResBody.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postCreateProfession(PostCreateOrUpdateProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateProfession reqBody{}", getClass().getSimpleName(), reqBody);

        boolean exists = professionRepo.findByCodeOrName(null, reqBody.getName()).isPresent();
        if (exists) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        ProfessionEntity professionEntity = ProfessionEntity.builder()
                .id(UUID.randomUUID().toString())
                .code("PRF000" + (professionRepo.count() + 1))
                .name(reqBody.getName())
                .status(reqBody.getStatus())
                .description(reqBody.getDescription())
                .createdBy(serviceUtils.getUserHeader(httpServletRequest))
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        professionRepo.save(professionEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postUpdateProfession(PostCreateOrUpdateProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateProfession reqBody{}", getClass().getSimpleName(), reqBody);

        Optional<ProfessionEntity> optionalCode = professionRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<ProfessionEntity> optionalName = professionRepo.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent() && !reqBody.getName().equals(optionalCode.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ProfessionEntity professionEntity = optionalCode.get();
        modelMapper.map(reqBody, professionEntity);
        professionEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        professionEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        professionRepo.save(professionEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public PostGetProfessionResBody postDetailProfession(PostHandleByCodeProfessionReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailProfession reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ProfessionEntity> optionalCode = professionRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalCode.get(), PostGetProfessionResBody.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postDeleteProfession(PostHandleByCodeProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteProfession reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ProfessionEntity> optionalCode = professionRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ProfessionEntity professionEntity = optionalCode.get();
        professionEntity.setStatus(StatusUtil.DELETED.name());
        professionEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        professionEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        professionRepo.save(professionEntity);
        return StatusUtil.SUCCESS.name();
    }

    private Specification<ProfessionEntity> professionSpec(PostListProfessionReqBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotBlank(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
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
