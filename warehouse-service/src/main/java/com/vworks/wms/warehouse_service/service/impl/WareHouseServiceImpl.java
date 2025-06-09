package com.vworks.wms.warehouse_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.model.responseBody.GetByUsernameResponseBody;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.admin_service.service.UserService;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.*;
import com.vworks.wms.warehouse_service.models.DetailWareHouseModel;
import com.vworks.wms.warehouse_service.models.SearchWareHouseModel;
import com.vworks.wms.warehouse_service.models.request.warehouse.*;
import com.vworks.wms.warehouse_service.models.response.SearchWareHouseResponseBody;
import com.vworks.wms.warehouse_service.models.response.warehouse.DetailMaterialForExBillModel;
import com.vworks.wms.warehouse_service.models.response.warehouse.DetailWareHouseForExBillResponseBody;
import com.vworks.wms.warehouse_service.models.response.warehouse.PostGetDetailWareHouseResponseBody;
import com.vworks.wms.warehouse_service.repository.*;
import com.vworks.wms.warehouse_service.service.WareHouseService;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WareHouseServiceImpl implements WareHouseService {
    private final WareHouseRepository wareHouseRepository;
    private final UserService userService;
    private final WareHouseDetailRepository wareHouseDetailRepository;
    private final MaterialsRepository materialsRepository;
    private final DetailMaterialsRepository detailMaterialsRepository;
    private final ProviderRepository providerRepository;
    private final ServiceUtils serviceUtils;
    private final UnitTypeRepository unitTypeRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public BaseResponse createWareHouse(CreateWareHouseRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} createWareHouse with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getWhCode())
                || StringUtils.isEmpty(requestBody.getWhName())
                || StringUtils.isEmpty(requestBody.getUserName())
                || StringUtils.isEmpty(requestBody.getPhoneNumber())
                || StringUtils.isEmpty(requestBody.getWhAddress())
                || StringUtils.isEmpty(requestBody.getWhDesc())
                || StringUtils.isEmpty(requestBody.getStatus())) {
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        WarehouseEntity warehouseEntityById = wareHouseRepository.findFirstByCode(requestBody.getWhCode());


        if (!Objects.isNull(warehouseEntityById)) {
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.WH_CODE_EXIST.getCode(), ExceptionTemplate.WH_CODE_EXIST.getMessage());
        }
        //check userId có tồn tại trong chức vụ thủ kho không
        GetByUsernameResponseBody usernameResponseBody = userService.getUserByUsername(requestBody.getUserName());
        if (Objects.isNull(usernameResponseBody)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.USERNAME_NOT_FOUND.getCode(), ExceptionTemplate.USER_NOT_FOUND.getMessage());
        }

        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setId(UUID.randomUUID().toString());
        warehouseEntity.setCode(requestBody.getWhCode());
        warehouseEntity.setName(requestBody.getWhName());
        warehouseEntity.setManagerWh(requestBody.getUserName());
        warehouseEntity.setPhoneNumberWh(requestBody.getPhoneNumber());
        warehouseEntity.setAddressWh(requestBody.getWhAddress());
        warehouseEntity.setDescription(requestBody.getWhDesc());
        warehouseEntity.setStatus(requestBody.getStatus());
        warehouseEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        warehouseEntity.setCreatedBy("sys");
        wareHouseRepository.save(warehouseEntity);

        log.info("[END] {} createWareHouse with warehouseEntity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(warehouseEntity));
        return new BaseResponse(StatusUtil.SUCCESS.name());
    }


    @Override
    public BaseResponse searchWareHouse(SearchWareHouseRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} with request body = {}", this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        SearchWareHouseResponseBody responseBody = new SearchWareHouseResponseBody();
        List<SearchWareHouseModel> searchWareHouseModelList = new ArrayList<>();
        List<WarehouseEntity> warehouseEntities = new ArrayList<>();
        Pageable page = serviceUtils.pageAble(requestBody.getPageNumber(), requestBody.getPageSize());
        if (StringUtils.isEmpty(requestBody.getStatus()) && StringUtils.isEmpty(requestBody.getKeyword())) {
            log.info("{} case findAll", this.getClass().getSimpleName());
            Page wareHousePage = wareHouseRepository.findAll(page);
            warehouseEntities = wareHousePage.getContent();
//            warehouseEntities =  wareHouseRepository.findAll();
            responseBody.setTotalPage(String.valueOf(wareHousePage.getTotalPages()));
        } else if (StringUtils.isEmpty(requestBody.getKeyword()) && !StringUtils.isEmpty(requestBody.getStatus())) {
            Page wareHousePage = wareHouseRepository.findAllByStatus(page, requestBody.getStatus());
            warehouseEntities = wareHousePage.getContent();
            responseBody.setTotalPage(String.valueOf(wareHousePage.getTotalPages()));
        } else if (!StringUtils.isEmpty(requestBody.getKeyword()) && StringUtils.isEmpty(requestBody.getStatus())) {
            log.info("case only keyword");
            Page wareHousePage = wareHouseRepository.findAllByCodeOrNameLike(page, requestBody.getKeyword(), requestBody.getKeyword());
            warehouseEntities = wareHousePage.getContent();
            responseBody.setTotalPage(String.valueOf(wareHousePage.getTotalPages()));
        } else {
            Page wareHousePage = wareHouseRepository.findAllByCodeOrNameContainsAndStatus(page, requestBody.getKeyword(), requestBody.getKeyword(), requestBody.getStatus());
            warehouseEntities = wareHousePage.getContent();
            responseBody.setTotalPage(String.valueOf(wareHousePage.getTotalPages()));
        }


        log.info("{} searchWareHouse with warehouseEntities = {}", this.getClass().getSimpleName(),
                new Gson().toJson(warehouseEntities));

        for (WarehouseEntity warehouseEntity : warehouseEntities) {
            SearchWareHouseModel searchWareHouseModel = new SearchWareHouseModel();
            searchWareHouseModel.setWhCode(warehouseEntity.getCode());
            searchWareHouseModel.setWhName(warehouseEntity.getName());
            searchWareHouseModel.setWhAddress(warehouseEntity.getAddressWh());
            searchWareHouseModel.setPhoneNumber(warehouseEntity.getPhoneNumberWh());
            searchWareHouseModel.setDesc(warehouseEntity.getDescription());
            searchWareHouseModel.setStatus(warehouseEntity.getStatus());
            //Lay full name by username
            GetByUsernameResponseBody usernameResponseBody = userService.getUserByUsername(warehouseEntity.getManagerWh());

            if (!Objects.isNull(usernameResponseBody)) {
                searchWareHouseModel.setManager(usernameResponseBody.getFullName());
            }

            searchWareHouseModelList.add(searchWareHouseModel);
        }
        responseBody.setSearchWareHouseModelList(searchWareHouseModelList);
        log.info("[END] {} searchWareHouse with response body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(responseBody));
        return new BaseResponse(responseBody);
    }

    @Override
    public BaseResponse updateWareHouse(UpdateWareHouseRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} updateWareHouse with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getWhCode())) {
            throw new WarehouseMngtSystemException(400, ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        WarehouseEntity warehouseEntity = wareHouseRepository.findFirstByCode(requestBody.getWhCode());

        if (Objects.isNull(warehouseEntity)) {
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.WH_CODE_NOT_FOUND.getCode(), ExceptionTemplate.WH_CODE_NOT_FOUND.getMessage());
        }

        if (!StringUtils.isEmpty(requestBody.getUserName())) {
            warehouseEntity.setName(requestBody.getWhName());
        }

        if (!StringUtils.isEmpty(requestBody.getWhAddress())) {
            warehouseEntity.setAddressWh(requestBody.getWhAddress());
        }

        if (!StringUtils.isEmpty(requestBody.getPhoneNumber())) {
            warehouseEntity.setPhoneNumberWh(requestBody.getPhoneNumber());
        }

        if (!StringUtils.isEmpty(requestBody.getWhDesc())) {
            warehouseEntity.setDescription(requestBody.getWhDesc());
        }

        if (!StringUtils.isEmpty(requestBody.getUserName())) {
            //Check username

            warehouseEntity.setManagerWh(requestBody.getUserName());
        }

        if (!StringUtils.isEmpty(requestBody.getStatus())) {
            warehouseEntity.setStatus(requestBody.getStatus());
        }

        wareHouseRepository.save(warehouseEntity);
        log.info("[END] {} updateWareHouse with warehouseEntity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(warehouseEntity));
        return new BaseResponse(StatusUtil.SUCCESS.name());
    }

    @Override
    public PostGetDetailWareHouseResponseBody getDetailWareHouse(PostGetWareHouseDetailRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} getDetailWareHouse with request body = {}", this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        String whCode = requestBody.getWhCode();
        if (StringUtils.isEmpty(whCode)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(),
                    ExceptionTemplate.INPUT_EMPTY.getCode(),
                    ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        List<DetailWareHouseModel> detailList = wareHouseDetailRepository.getDetailByWarehouseCode(whCode);

        if (detailList.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(),
                    ExceptionTemplate.WH_CODE_NOT_FOUND.getCode(),
                    ExceptionTemplate.WH_CODE_NOT_FOUND.getMessage());
        }

        PostGetDetailWareHouseResponseBody responseBody = new PostGetDetailWareHouseResponseBody();
        responseBody.setProductList(detailList);

        return responseBody;
    }


    @Override
    public SearchWareHouseResponseBody searchWareHouseV2(SearchWareHouseRequestBody requestBody) throws WarehouseMngtSystemException {
        int page = 1;
        int limit = 10;
        if (requestBody.getPageNumber() != null && StringUtils.isNotEmpty(requestBody.getPageNumber())) {
            page = Integer.parseInt(requestBody.getPageNumber());
        }

        if (requestBody.getPageSize() != null && StringUtils.isNotEmpty(requestBody.getPageSize())) {
            limit = Integer.parseInt(requestBody.getPageSize());
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<WarehouseEntity> warehouseEntities = wareHouseRepository.findAll(specificationWareHouse(requestBody), pageable);


        List<SearchWareHouseModel> wareHouseModels = warehouseEntities.getContent().stream().map(x -> {
            return SearchWareHouseModel.builder()
                    .whName(x.getName())
                    .whCode(x.getCode())
                    .status(x.getStatus())
                    .phoneNumber(x.getPhoneNumberWh())
                    .desc(x.getDescription())
                    .whAddress(x.getAddressWh())
                    .manager(userInfoRepository.findFirstByUserCodeOrUserId(x.getManagerWh(), x.getManagerWh()).map(UserInfoEntity::getFullName).orElse(""))
                    .userCodeManager(x.getManagerWh())
                    .build();
        }).toList();

        return SearchWareHouseResponseBody.builder()
                .searchWareHouseModelList(wareHouseModels)
                .totalPage(String.valueOf(warehouseEntities.getTotalPages()))
                .totalElement(warehouseEntities.getTotalElements())
                .build();
    }

    @Override
    public DetailWareHouseForExBillResponseBody detailWareHouse(DetailWareHouseForExBillRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} detailWareHouse with whCode = {}", this.getClass().getSimpleName(), requestBody.getWhCode());
        if (StringUtils.isEmpty(requestBody.getWhCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }
        List<WarehouseDetailEntity> warehouseDetailEntityList = wareHouseDetailRepository.findAllByWarehouseCode(requestBody.getWhCode());
        WarehouseEntity warehouseEntity = wareHouseRepository.findFirstByCode(requestBody.getWhCode());
        List<String> materialCodeList = warehouseDetailEntityList.stream().map(WarehouseDetailEntity::getMaterialCode).toList();
        List<DetailMaterialsEntity> detailMaterialsEntities = detailMaterialsRepository.findAllByCodeIn(materialCodeList);
        List<DetailMaterialForExBillModel> detailMaterialForExBillModels = detailMaterialsEntities.stream().map(x -> {
            return DetailMaterialForExBillModel.builder()
                    .materialCode(x.getCode())
                    .materialName(x.getName())
                    .materialType(materialsRepository.findByCodeOrName(x.getMaterialTypeCode(), null).map(MaterialsEntity::getName).orElse(""))
                    .unit(unitTypeRepository.findByCodeOrName(x.getMeasureKeyword(), x.getMeasureKeyword()).map(UnitTypeEntity::getName).orElse(""))
//                    .param(x.getParameter())
                    .quantity(wareHouseDetailRepository.findFirstByMaterialCode(x.getCode()).map(WarehouseDetailEntity::getQuantity).orElse(0))
                    .price(String.valueOf(x.getListPrice()))
                    .build();
        }).toList();
        return DetailWareHouseForExBillResponseBody.builder()
                .whCode(warehouseEntity.getCode())
                .whName(warehouseEntity.getName())
                .detailMaterial(detailMaterialForExBillModels)
                .build();
    }

    private Specification<WarehouseEntity> specificationWareHouse(SearchWareHouseRequestBody requestBody) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), requestBody.getStatus()));
            }
            String keyword = "%" + requestBody.getKeyword() + "%";
            if (StringUtils.isNotEmpty(requestBody.getKeyword())) {
                Predicate code = criteriaBuilder.like(root.get("code"), keyword);
                Predicate name = criteriaBuilder.like(root.get("name"), keyword);
                predicates.add(criteriaBuilder.or(code, name));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
