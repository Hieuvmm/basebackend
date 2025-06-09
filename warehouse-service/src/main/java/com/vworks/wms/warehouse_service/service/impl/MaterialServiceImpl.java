package com.vworks.wms.warehouse_service.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.common_lib.config.MinioConfigProperties;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.MinioService;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.*;
import com.vworks.wms.warehouse_service.models.request.DetailWholesalePrice;
import com.vworks.wms.warehouse_service.models.request.ParametersMaterial;
import com.vworks.wms.warehouse_service.models.request.material.*;
import com.vworks.wms.warehouse_service.models.response.material.*;
import com.vworks.wms.warehouse_service.repository.*;
import com.vworks.wms.warehouse_service.service.MaterialService;
import com.vworks.wms.warehouse_service.utils.Commons;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private final DetailMaterialsRepository detailMaterialsRepository;
    private final MaterialsRepository materialsRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final MinioConfigProperties minioConfigProperties;
    private final Gson gson;
    private final WareHouseDetailRepository wareHouseDetailRepository;
    private final ParameterRepository parameterRepository;
    private final ParameterTypeRepository parameterTypeRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Page<PostListMaterialResponse> postListMaterial(PostListMaterialRequest requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postListMaterial requestBody {}", getClass().getSimpleName(), gson.toJson(requestBody));
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), Sort.by("createdDate").descending());
        List<String> materialCodeList = null;
        if (StringUtils.isNotEmpty(requestBody.getWhCode())) {
            materialCodeList = wareHouseDetailRepository.findAllByWarehouseCode(requestBody.getWhCode()).stream().map(WarehouseDetailEntity::getMaterialCode).toList();
            log.info("{} postListMaterial with whCode = {} have materialCode list = {}", this.getClass().getSimpleName(), requestBody.getWhCode(), materialCodeList);
        }
        Page<DetailMaterialsEntity> page = detailMaterialsRepository.findAll(detailMaterialsSpec(requestBody, materialCodeList), pageable);
        String username = StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null;

        List<PostListMaterialResponse> list = page.getContent().stream().map(e ->
                {

                    PostListMaterialResponse x =
                            modelMapper.map(e, PostListMaterialResponse.class);
                    x.setUnit(unitTypeRepository.findByCodeOrName(e.getMeasureKeyword(), e.getMeasureKeyword()).map(UnitTypeEntity::getName).orElse(""));
                    x.setMaterialType(materialsRepository.findByCodeOrName(e.getMaterialTypeCode(), e.getMaterialTypeCode()).map(MaterialsEntity::getName).orElse(""));
                    x.setDiscountMaterialModel(getDiscountModel(e.getDiscount(), username));
                    log.info("{} postListMaterial convert with string json = {}", this.getClass().getSimpleName(), e.getParameters());
                    x.setParameterModels(mapParameter(e.getParameters()));
                    return x;
                }
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Override
    public DiscountMaterialModel getDiscountModel(String discount, String username) {
        log.info("{} getDiscountModel with discount = {} and username = {}", this.getClass().getSimpleName(), gson.toJson(discount), username);
        Type listType = new TypeToken<ArrayList<DiscountMaterialModel>>() {
        }.getType();
        List<DiscountMaterialModel> discountMaterialModelList = gson.fromJson(discount, listType);
        Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findFirstByUserCodeOrUserId(username, username);
        DiscountMaterialModel discountMaterialModel = new DiscountMaterialModel();
        if (userInfoEntity.isEmpty()) {
            log.info("{} getDiscountModel with userInfoEntity is empty", this.getClass().getSimpleName());
            return discountMaterialModel;
        }

        for (DiscountMaterialModel e : discountMaterialModelList) {
            if (e.getPositionCode().equals(userInfoEntity.get().getJobPositionCode())) {
                discountMaterialModel = e;
            }
        }
        return discountMaterialModel;
    }

    @Override
    public List<ParameterModel> mapParameter(String e) {
        Type listType = new TypeToken<ArrayList<ParameterModel>>() {
        }.getType();
        List<ParameterModel> parameterModelList = gson.fromJson(e, listType);
        parameterModelList.stream().peek(x -> {
            x.setParameterTypeName(parameterTypeRepository.findByCodeOrName(x.getParameterTypeCode(), x.getParameterTypeCode()).map(ParameterTypeEntity::getName).orElse(""));
            x.setParameterValue(parameterRepository.findByCodeOrName(x.getParameterCode(), x.getParameterCode()).map(ParameterEntity::getName).orElse(""));
        }).toList();
        return parameterModelList;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostCreateMaterialResponse postCreateMaterial(PostCreateMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateMaterial requestBody {}", getClass().getSimpleName(), requestBody);

        Optional<DetailMaterialsEntity> optionalDetailMaterialsCode = detailMaterialsRepository.findByCodeOrName(requestBody.getCode(), null);

        if (optionalDetailMaterialsCode.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<DetailMaterialsEntity> optionalDetailMaterialsName = detailMaterialsRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalDetailMaterialsName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        Optional<MaterialsEntity> optionalMaterials = materialsRepository.findByCodeOrName(requestBody.getMaterialTypeCode(), null);
        if (optionalMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        Optional<UnitTypeEntity> optionalUnitType = unitTypeRepository.findByCodeOrName(requestBody.getUnitTypeCode(), null);
        if (optionalUnitType.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        List<String> pathFileImages = new ArrayList<>();
        int index = 1;

        for (MultipartFile file : requestBody.getImages()) {
            String pathFileImage = minioService.uploadImageMaterialToMinio(
                    file,
                    minioConfigProperties.getBucketName(),
                    minioConfigProperties.getMaterialImageFolderStorage(),
                    requestBody.getCode(),
                    requestBody.getCode() + "_" + index // Tạo tên file tăng dần
            );
            pathFileImages.add(pathFileImage);
            index++;
        }


        DetailMaterialsEntity detailMaterialsEntity = new DetailMaterialsEntity();
        detailMaterialsEntity.setId(UUID.randomUUID().toString());
        detailMaterialsEntity.setCode(requestBody.getCode());
        detailMaterialsEntity.setName(requestBody.getName());
        detailMaterialsEntity.setMaterialTypeCode(requestBody.getMaterialTypeCode());
        detailMaterialsEntity.setMeasureKeyword(requestBody.getUnitTypeCode());
        detailMaterialsEntity.setListPrice(requestBody.getListPrice());
        detailMaterialsEntity.setSellPrice(requestBody.getSellPrice());
        detailMaterialsEntity.setOrigin(requestBody.getOrigin());
        detailMaterialsEntity.setMinInventory(requestBody.getMinInventory());
        detailMaterialsEntity.setDiscount(gson.toJson(requestBody.getDetailWholesalePrice()));
        detailMaterialsEntity.setParameters(gson.toJson(requestBody.getParametersMaterials()));
        if (!CollectionUtils.isEmpty(pathFileImages)) {
            detailMaterialsEntity.setImage(gson.toJson(pathFileImages));
        }
        detailMaterialsEntity.setStatus(requestBody.getStatus());
        detailMaterialsEntity.setDescription(requestBody.getDescription());
        detailMaterialsEntity.setCreatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        detailMaterialsEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        detailMaterialsRepository.save(detailMaterialsEntity);
        return modelMapper.map(detailMaterialsEntity, PostCreateMaterialResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostUpdateMaterialResponse postUpdateMaterial(PostUpdateMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateMaterial requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<DetailMaterialsEntity> optionalDetailMaterials = detailMaterialsRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalDetailMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<DetailMaterialsEntity> optionalDetailMaterialsCode = detailMaterialsRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalDetailMaterialsCode.isPresent() && !StringUtils.equals(optionalDetailMaterialsCode.get().getCode(), optionalDetailMaterials.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<DetailMaterialsEntity> optionalDetailMaterialsName = detailMaterialsRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalDetailMaterialsName.isPresent() && !StringUtils.equals(optionalDetailMaterialsName.get().getName(), optionalDetailMaterials.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        Optional<MaterialsEntity> optionalMaterials = materialsRepository.findByCodeOrName(requestBody.getMaterialTypeCode(), null);
        if (optionalMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        Optional<UnitTypeEntity> optionalUnitType = unitTypeRepository.findByCodeOrName(requestBody.getUnitTypeCode(), null);
        if (optionalUnitType.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }


        List<String> pathFileImages = new ArrayList<>();
        int index = 1;

        for (MultipartFile file : requestBody.getImages()) {
            String pathFileImage = minioService.uploadImageMaterialToMinio(
                    file,
                    minioConfigProperties.getBucketName(),
                    minioConfigProperties.getMaterialImageFolderStorage(),
                    requestBody.getCode(),
                    requestBody.getCode() + "_" + index
            );
            pathFileImages.add(pathFileImage);
            index++;
        }

        DetailMaterialsEntity detailMaterialsEntity = optionalDetailMaterials.get();
        detailMaterialsEntity.setCode(requestBody.getCode());
        detailMaterialsEntity.setName(requestBody.getName());
        detailMaterialsEntity.setMaterialTypeCode(requestBody.getMaterialTypeCode());
        detailMaterialsEntity.setMeasureKeyword(requestBody.getUnitTypeCode());
        detailMaterialsEntity.setListPrice(requestBody.getListPrice());
        detailMaterialsEntity.setSellPrice(requestBody.getSellPrice());
        detailMaterialsEntity.setParameters(gson.toJson(requestBody.getParametersMaterials()));
        detailMaterialsEntity.setOrigin(requestBody.getOrigin());
        detailMaterialsEntity.setMinInventory(requestBody.getMinInventory());
        detailMaterialsEntity.setDiscount(gson.toJson(requestBody.getDetailWholesalePrice()));
        if (!CollectionUtils.isEmpty(pathFileImages)) {
            detailMaterialsEntity.setImage(gson.toJson(pathFileImages));
        }
        detailMaterialsEntity.setStatus(requestBody.getStatus());
        detailMaterialsEntity.setDescription(requestBody.getDescription());
        detailMaterialsEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        detailMaterialsEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        detailMaterialsRepository.save(detailMaterialsEntity);

        return modelMapper.map(detailMaterialsEntity, PostUpdateMaterialResponse.class);
    }

    @Override
    public PostDetailMaterialResponse postDetailMaterial(PostDetailMaterialRequest requestBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailMaterial requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<DetailMaterialsEntity> optionalDetailMaterials = detailMaterialsRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalDetailMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        DetailMaterialsEntity detailMaterialsEntity = optionalDetailMaterials.get();

        PostDetailMaterialResponse response = modelMapper.map(detailMaterialsEntity, PostDetailMaterialResponse.class);
        response.setImage(gson.fromJson(detailMaterialsEntity.getImage(), new TypeToken<List<String>>() {
        }.getType()));
        response.setUnitTypeCode(detailMaterialsEntity.getMeasureKeyword());
        response.setDetailWholesalePrice(gson.fromJson(detailMaterialsEntity.getDiscount(), new TypeToken<List<DetailWholesalePrice>>() {
        }.getType()));
        response.setParametersMaterials(gson.fromJson(detailMaterialsEntity.getParameters(), new TypeToken<List<ParametersMaterial>>() {
        }.getType()));

        return response;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostDeleteMaterialResponse postDeleteMaterial(PostDeleteMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteMaterial requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<DetailMaterialsEntity> optionalDetailMaterials = detailMaterialsRepository.findById(requestBody.getId());
        if (optionalDetailMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        if (StringUtils.equals(optionalDetailMaterials.get().getStatus(), StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        DetailMaterialsEntity detailMaterialsEntity = optionalDetailMaterials.get();
        detailMaterialsEntity.setStatus(StatusUtil.DELETED.name());
        detailMaterialsRepository.save(detailMaterialsEntity);
        return modelMapper.map(detailMaterialsEntity, PostDeleteMaterialResponse.class);
    }

    @Override
    public List<PostGetByConditionResponseBody> postGetByCondition(PostGetByConditionRequestBody requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postGetByCondition requestBody {}", getClass().getSimpleName(), requestBody);
        List<DetailMaterialsEntity> detailMaterialsEntities = detailMaterialsRepository.findAllByMaterialTypeCode(requestBody.getMaterialTypeCode());
        List<MaterialsEntity> materialsEntities = materialsRepository.findAll();
        List<UnitTypeEntity> unitTypeEntities = unitTypeRepository.findAll();
        return detailMaterialsEntities.stream().map(e -> {
                    List<DetailWholesalePrice> detailWholesalePrices = gson.fromJson(e.getDiscount(), new TypeToken<List<DetailWholesalePrice>>() {
                    }.getType());
                    MaterialsEntity materialsEntity = materialsEntities.stream().filter(f -> StringUtils.equals(f.getCode(), e.getMaterialTypeCode())).findFirst().orElse(null);
                    UnitTypeEntity unitTypeEntity = unitTypeEntities.stream().filter(f -> StringUtils.equals(f.getCode(), e.getMeasureKeyword())).findFirst().orElse(null);
                    return PostGetByConditionResponseBody.builder()
                            .id(e.getId())
                            .code(e.getCode())
                            .name(e.getName())
                            .materialTypeCode(materialsEntity.getCode())
                            .materialTypeName(materialsEntity.getName())
                            .unitTypeCode(unitTypeEntity.getCode())
                            .unitTypeName(unitTypeEntity.getName())
//                            .parameter(e.getParameter())
                            .price(StringUtils.isBlank(requestBody.getPositionCode()) ? e.getListPrice() : getPrice(detailWholesalePrices, e.getListPrice(), requestBody.getPositionCode()))
                            .build();
                }

        ).toList();
    }

    @Override
    public Object postDetailMaterialList(PostDetailMaterialListRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDetailMaterialList requestBody {}", getClass().getSimpleName(), requestBody);
        List<DetailMaterialsEntity> detailMaterialsEntityList = detailMaterialsRepository.findAllByCodeIn(requestBody.getCodeList());
        if (CollectionUtils.isEmpty(detailMaterialsEntityList)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        return detailMaterialsEntityList.stream().map(detailMaterialsEntity -> PostDetailMaterialResponse.builder()
                        .id(detailMaterialsEntity.getId())
                        .code(detailMaterialsEntity.getCode())
                        .name(detailMaterialsEntity.getName())
                        .materialTypeCode(detailMaterialsEntity.getMaterialTypeCode())
                        .unitTypeCode(detailMaterialsEntity.getMeasureKeyword())
                        .listPrice(detailMaterialsEntity.getListPrice())
                        .minInventory(detailMaterialsEntity.getMinInventory())
                        .origin(detailMaterialsEntity.getOrigin())
                        .detailWholesalePrice(gson.fromJson(detailMaterialsEntity.getDiscount(), new TypeToken<List<DetailWholesalePrice>>() {
                        }.getType()))
                        .image(gson.fromJson(detailMaterialsEntity.getImage(), new TypeToken<List<String>>() {
                        }.getType()))
                        .description(detailMaterialsEntity.getDescription())
                        .status(detailMaterialsEntity.getStatus())
                        .createdBy(detailMaterialsEntity.getCreatedBy())
                        .createdDate(detailMaterialsEntity.getCreatedDate())
                        .updatedBy(detailMaterialsEntity.getUpdatedBy())
                        .updatedDate(detailMaterialsEntity.getUpdatedDate())
                        .build())
                .toList();
    }

    private BigDecimal getPrice(List<DetailWholesalePrice> detailWholesalePrices, BigDecimal listPrice, String positionCode) {
        if (CollectionUtils.isEmpty(detailWholesalePrices)) {
            return listPrice;
        } else {
            List<DetailWholesalePrice> detailWholesaleFilterPosition = detailWholesalePrices.stream().filter(e -> StringUtils.equals(e.getPositionCode(), positionCode)).toList();
            if (CollectionUtils.isEmpty(detailWholesaleFilterPosition)) {
                return listPrice;
            } else {
                return detailWholesaleFilterPosition.get(0).getValue();
            }
        }
    }

    private Specification<DetailMaterialsEntity> detailMaterialsSpec(PostListMaterialRequest request, List<String> materalCodeList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotBlank(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.isNotEmpty(request.getWhCode())) {
                predicates.add(criteriaBuilder.in(root.get("code")).value(materalCodeList));
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
