package com.vworks.wms.warehouse_service.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.*;
import com.vworks.wms.warehouse_service.models.ApprovedDetailModel;
import com.vworks.wms.warehouse_service.models.request.importBill.*;
import com.vworks.wms.warehouse_service.models.request.importBill.detailImportMaterials.DetailImportMaterial;
import com.vworks.wms.warehouse_service.models.response.importBill.*;
import com.vworks.wms.warehouse_service.repository.*;
import com.vworks.wms.warehouse_service.service.ImportBillService;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportBillServiceImpl implements ImportBillService {
    private final ImExBillRepository imExBillRepository;
    private final ImExDetailBillRepository imExDetailBillRepository;
    private final ProviderRepository providerRepository;
    private final WareHouseRepository wareHouseRepository;
    private final ModelMapper modelMapper;
    private final ExchangeRateRepository exchangeRateRepository;
    private final DetailMaterialsRepository detailMaterialsRepository;
    private final MaterialsRepository materialsRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final UserInfoRepository userInfoRepository;
    private final ServiceUtils serviceUtils;
    private final Gson gson;
    private final WareHouseDetailRepository wareHouseDetailRepository;

    @Override
    public Page<PostListImportBillResponseBody> postListImportBill(PostListImportBillRequestBody requestBody) {
        log.info("{} postListImportBill requestBody {}", getClass().getSimpleName(), requestBody);

        Sort sort = Sort.by(Sort.Direction.DESC, Commons.FIELD_CREATED_DATE);
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), sort);
        Page<ImExBillEntity> imExBillEntityPage = imExBillRepository.findAll(imExBillsSpec(requestBody), pageable);

        List<PostListImportBillResponseBody> importBills = imExBillEntityPage.getContent().stream().map(e ->
                PostListImportBillResponseBody.builder()
                        .id(e.getId())
                        .code(e.getCode())
                        .totalBill(e.getTotalPrice())
                        .providerName(providerRepository.findByCodeOrName(e.getProviderCode(), null).map(ProviderEntity::getName).orElse(""))
                        .warehouseName(wareHouseRepository.findByCode(e.getWhCode()).map(WarehouseEntity::getName).orElse(""))
                        .status(e.getStatus())
                        .description(e.getDescription())
                        .createdDate(e.getCreatedDate())
                        .createdBy(e.getCreatedBy())
                        .build()
        ).toList();

        return new PageImpl<>(importBills, pageable, imExBillEntityPage.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostCreateImportBillResponseBody postCreateImportBill(PostCreateImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateImportBill requestBody {}", getClass().getSimpleName(), requestBody);

        boolean checkExistBill = imExBillRepository.existsByCode(requestBody.getCode());
        if (checkExistBill) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        providerRepository.findByCodeOrName(requestBody.getProviderCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        wareHouseRepository.findByCode(requestBody.getWarehouseCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        exchangeRateRepository.findByCodeOrName(requestBody.getExchangeRateCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (CollectionUtils.isEmpty(requestBody.getDetailImportMaterials())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        BigDecimal totalBill = requestBody.getDetailImportMaterials().stream()
                .map(e -> {
                    BigDecimal sellPrice = e.getPrice();
                    BigDecimal quantity = BigDecimal.valueOf(Long.parseLong(String.valueOf(e.getRealQuantity())));
                    return sellPrice.multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ImExBillEntity imExBillEntity = ImExBillEntity.builder()
                .id(UUID.randomUUID().toString())
                .code(requestBody.getCode())
                .orderCode(requestBody.getOrderCode())
                .importDate(requestBody.getImportDate())
                .orderDate(requestBody.getOrderDate())
                .content(requestBody.getImportContent())
                .providerCode(requestBody.getProviderCode())
                .whCode(requestBody.getWarehouseCode())
                .deliveryMethod(requestBody.getDeliveryMethod())
                .exchangeRateCode(requestBody.getExchangeRateCode())
                .attachment(null)
                .description(requestBody.getDescription())
                .status(StatusUtil.NEW.name())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .createdBy(StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(Commons.FIELD_USER_ID) : null)
                .totalPrice(totalBill)
//                .attachment(serviceUtils.postUploadImages(requestBody.getAttachment(), requestBody.getCode(), ""))
                .type("IM_BILL")
                .build();
        imExBillRepository.save(imExBillEntity);

        List<ImExDetailEntity> imExDetails = requestBody.getDetailImportMaterials().stream().map(e ->
                ImExDetailEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .billCode(imExBillEntity.getCode())
                        .materialCode(e.getMaterialCode())
                        .materialTypeCode(e.getMaterialTypeCode())
                        .realQuantity(e.getRealQuantity())
                        .expectedQuantity(e.getExpectedQuantity())
                        .price(e.getPrice())
                        .unitTypeCode(e.getUnitTypeCode())
                        .createdBy(StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(Commons.FIELD_USER_ID) : null)
                        .createdDate(new Timestamp(System.currentTimeMillis()))
                        .build()
        ).toList();
        imExDetailBillRepository.saveAll(imExDetails);
        return PostCreateImportBillResponseBody.builder()
                .code(imExBillEntity.getCode())
                .providerName(providerRepository.findByCodeOrName(imExBillEntity.getProviderCode(), null).map(ProviderEntity::getName).orElse(""))
                .warehouseName(wareHouseRepository.findByCode(imExBillEntity.getWhCode()).map(WarehouseEntity::getName).orElse(""))
                .totalBill(totalBill)
                .createdBy(imExBillEntity.getCreatedBy())
                .createdDate(imExBillEntity.getCreatedDate())
                .status(imExBillEntity.getStatus())
                .description(imExBillEntity.getDescription())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostAssignApprovalResponseBody postAssignApprovalImportBill(PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postAssignApprovalImportBill requestBody {}", getClass().getSimpleName(), requestBody);

        if (CollectionUtils.isEmpty(requestBody.getImportBillCodes()) || CollectionUtils.isEmpty(requestBody.getApproves()) || CollectionUtils.isEmpty(requestBody.getFollows())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        List<ImExBillEntity> imExBills = imExBillRepository.findAllByCodeIn(requestBody.getImportBillCodes());

        boolean checkStatus = imExBills.stream().anyMatch(e -> StringUtils.equals(e.getStatus(), StatusUtil.REVIEWING.name()));
        if (checkStatus) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }

        if (imExBills.size() != requestBody.getImportBillCodes().size()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        if (!Collections.disjoint(requestBody.getApproves(), requestBody.getFollows())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        List<String> userIds = new ArrayList<>();
        userIds.addAll(requestBody.getApproves());
        userIds.addAll(requestBody.getFollows());
        boolean checkExist = userIds.stream().allMatch(userInfoRepository::existsByUserId);
        if (!checkExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        String userId = StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(Commons.FIELD_USER_ID) : null;
        UserInfoEntity userInfo = userInfoRepository.findByUserId(userId).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        List<ImExBillEntity> imExBillAssigns = imExBills.stream().peek(e -> {
            ApprovedDetailModel approvedDetailModel = ApprovedDetailModel.builder()
                    .userName(userInfo.getFullName())
                    .userId(userInfo.getUserId())
                    .status(StatusUtil.ASSIGN_APPR0VAL.name())
                    .approveTime(serviceUtils.convertTimeStampToString(new Timestamp(System.currentTimeMillis())))
                    .build();
            e.setApproveDetail(String.join(",", requestBody.getApproves()));
            e.setFollowDetail(String.join(",", requestBody.getFollows()));
            e.setStatus(StatusUtil.REVIEWING.name());
            e.setApprovedDetail(gson.toJson(Collections.singletonList(approvedDetailModel)));
        }).toList();

        imExBillRepository.saveAll(imExBillAssigns);
        return PostAssignApprovalResponseBody.builder().status(StatusUtil.SUCCESS.name()).build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostChangeStatusImportBillResponseBody postChangeStatusImportBill(PostChangeStatusImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteImportBill requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<ImExBillEntity> imExBillEntityOptional = imExBillRepository.findById(requestBody.getId());
        if (imExBillEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ImExBillEntity imExBillEntity = imExBillEntityOptional.get();
        if (!imExBillEntity.getStatus().equals(StatusUtil.NEW.name()) && requestBody.getStatus().equals(StatusUtil.CANCELED.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        if (imExBillEntity.getStatus().equals(StatusUtil.REVIEWING.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        imExBillEntity.setStatus(requestBody.getStatus());
        imExBillRepository.save(imExBillEntity);

        return modelMapper.map(imExBillEntity, PostChangeStatusImportBillResponseBody.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostUpdateImportBillResponseBody postUpdateImportBill(PostUpdateImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateImportBill requestBody {}", getClass().getSimpleName(), requestBody);
        ImExBillEntity imExBillEntity = imExBillRepository.findById(requestBody.getId()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (!StringUtils.equals(imExBillEntity.getStatus(), StatusUtil.NEW.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        Optional<ImExBillEntity> imExBillEntityByCode = imExBillRepository.findByCode(requestBody.getCode());

        if (imExBillEntityByCode.isPresent() && !StringUtils.equals(imExBillEntity.getCode(), imExBillEntityByCode.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        providerRepository.findByCodeOrName(requestBody.getProviderCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        wareHouseRepository.findByCode(requestBody.getWarehouseCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        exchangeRateRepository.findByCodeOrName(requestBody.getExchangeRateCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (CollectionUtils.isEmpty(requestBody.getDetailImportMaterials())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        BigDecimal totalBill = requestBody.getDetailImportMaterials().stream()
                .map(e -> {
                    BigDecimal sellPrice = e.getPrice();
                    BigDecimal quantity = BigDecimal.valueOf(Long.parseLong(String.valueOf(e.getRealQuantity())));
                    return sellPrice.multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        imExBillEntity.setCode(requestBody.getCode());
        imExBillEntity.setOrderCode(requestBody.getOrderCode());
        imExBillEntity.setImportDate(requestBody.getImportDate());
        imExBillEntity.setOrderDate(requestBody.getOrderDate());
        imExBillEntity.setContent(requestBody.getImportContent());
        imExBillEntity.setProviderCode(requestBody.getProviderCode());
        imExBillEntity.setWhCode(requestBody.getWarehouseCode());
        imExBillEntity.setDeliveryMethod(requestBody.getDeliveryMethod());
        imExBillEntity.setExchangeRateCode(requestBody.getExchangeRateCode());
        imExBillEntity.setAttachment(null);
        imExBillEntity.setDescription(requestBody.getDescription());
        imExBillEntity.setTotalPrice(totalBill);
        imExBillEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillEntity.setUpdatedBy(StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(Commons.FIELD_USER_ID) : null);

        imExBillRepository.save(imExBillEntity);

        imExDetailBillRepository.deleteAllByBillCode(requestBody.getCode());

        List<ImExDetailEntity> imExDetails = requestBody.getDetailImportMaterials().stream().map(e ->
                ImExDetailEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .billCode(imExBillEntity.getCode())
                        .materialCode(e.getMaterialCode())
                        .materialTypeCode(e.getMaterialTypeCode())
                        .realQuantity(e.getRealQuantity())
                        .expectedQuantity(e.getExpectedQuantity())
                        .price(e.getPrice())
                        .unitTypeCode(e.getUnitTypeCode())
                        .createdBy(StringUtils.isNotEmpty(httpServletRequest.getHeader(Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(Commons.FIELD_USER_ID) : null)
                        .createdDate(new Timestamp(System.currentTimeMillis()))
                        .build()
        ).toList();
        imExDetailBillRepository.saveAll(imExDetails);
        return PostUpdateImportBillResponseBody.builder()
                .id(imExBillEntity.getId())
                .code(imExBillEntity.getCode())
                .providerName(providerRepository.findByCodeOrName(imExBillEntity.getProviderCode(), null).map(ProviderEntity::getName).orElse(""))
                .warehouseName(wareHouseRepository.findByCode(imExBillEntity.getWhCode()).map(WarehouseEntity::getName).orElse(""))
                .totalBill(totalBill)
                .createdBy(imExBillEntity.getCreatedBy())
                .createdDate(imExBillEntity.getCreatedDate())
                .status(imExBillEntity.getStatus())
                .description(imExBillEntity.getDescription())
                .build();
    }

    @Override
    public PostDetailImportBillResponseBody postDetailImportBill(PostDetailImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDetailImportBill requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<ImExBillEntity> imExBillEntityOptional = imExBillRepository.findByCode(requestBody.getCode());
        if (imExBillEntityOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ImExBillEntity imExBillEntity = imExBillEntityOptional.get();
        List<ImExDetailEntity> imExDetailEntities = imExDetailBillRepository.findAllByBillCode(imExBillEntity.getCode());

        if (CollectionUtils.isEmpty(imExDetailEntities)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        List<DetailMaterialsEntity> detailMaterialList = detailMaterialsRepository.findAllByCodeIn(imExDetailEntities.stream().map(ImExDetailEntity::getMaterialCode).toList());
        List<MaterialsEntity> materialsEntities = materialsRepository.findAllByCodeIn(detailMaterialList.stream().map(DetailMaterialsEntity::getMaterialTypeCode).toList());
        List<UnitTypeEntity> unitTypeEntities = unitTypeRepository.findAllByCodeIn(detailMaterialList.stream().map(DetailMaterialsEntity::getMeasureKeyword).toList());
        List<DetailImportMaterial> detailImportMaterials = imExDetailEntities.stream().map(e ->
                {
                    DetailMaterialsEntity detailMaterials = detailMaterialList.stream().filter(item -> StringUtils.equals(e.getMaterialCode(), item.getCode())).findFirst().orElse(null);
                    MaterialsEntity materialsEntity = materialsEntities.stream().filter(item -> StringUtils.equals(e.getMaterialTypeCode(), item.getCode())).findFirst().orElse(null);
                    UnitTypeEntity unitTypeEntity = unitTypeEntities.stream().filter(item -> StringUtils.equals(e.getUnitTypeCode(), item.getCode())).findFirst().orElse(null);
                    return DetailImportMaterial.builder()
                            .materialCode(e.getMaterialCode())
                            .materialName(detailMaterials != null ? detailMaterials.getName() : null)
                            .materialTypeCode(e.getMaterialTypeCode())
                            .materialTypeName(materialsEntity != null ? materialsEntity.getName() : null)
                            .unitTypeCode(e.getUnitTypeCode())
                            .unitTypeName(unitTypeEntity != null ? unitTypeEntity.getName() : null)
//                            .parameter(detailMaterials != null ? detailMaterials.getParameter() : null)
                            .expectedQuantity(e.getExpectedQuantity())
                            .realQuantity(e.getRealQuantity())
                            .price(e.getPrice())
                            .build();
                }
        ).toList();
        UserInfoEntity userInfo = userInfoRepository.findByUserId(imExBillEntity.getCreatedBy()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        List<ApprovedDetailModel> approvedDetailModels = new ArrayList<>();
        approvedDetailModels.add(ApprovedDetailModel.builder()
                .userId(userInfo.getUserId())
                .userName(userInfo.getFullName())
                .approveTime(serviceUtils.convertTimeStampToString(imExBillEntity.getCreatedDate()))
                .status(StatusUtil.NEW.name())
                .build());

        String approvedDetail = imExBillEntity.getApprovedDetail();
        if (StringUtils.isNotBlank(approvedDetail)) {
            approvedDetailModels.addAll(
                    gson.fromJson(approvedDetail, new TypeToken<List<ApprovedDetailModel>>() {
                    }.getType())
            );
        }
        return PostDetailImportBillResponseBody.builder()
                .id(imExBillEntity.getId())
                .code(imExBillEntity.getCode())
                .orderCode(imExBillEntity.getOrderCode())
                .importDate(imExBillEntity.getImportDate())
                .orderDate(imExBillEntity.getOrderDate())
                .importContent(imExBillEntity.getContent())
                .providerCode(imExBillEntity.getProviderCode())
                .warehouseCode(imExBillEntity.getWhCode())
                .deliveryMethod(imExBillEntity.getDeliveryMethod())
                .exchangeRateCode(imExBillEntity.getExchangeRateCode())
                .description(imExBillEntity.getDescription())
                .approveDetail(imExBillEntity.getApproveDetail())
                .totalPrice(imExBillEntity.getTotalPrice())
                .attachment(imExBillEntity.getAttachment())
                .followDetail(imExBillEntity.getFollowDetail())
                .approvedDetail(CollectionUtils.isEmpty(approvedDetailModels) ? null : approvedDetailModels)
                .detailImportMaterials(CollectionUtils.isEmpty(detailImportMaterials) ? null : detailImportMaterials)
                .approvals(StringUtils.isNotEmpty(imExBillEntity.getApproveDetail()) ? userInfoRepository.findByUserIdIn(List.of(imExBillEntity.getApproveDetail().split(","))) : null)
                .follows(StringUtils.isNotEmpty(imExBillEntity.getFollowDetail()) ? userInfoRepository.findByUserIdIn(List.of(imExBillEntity.getFollowDetail().split(","))) : null)
                .status(imExBillEntity.getStatus())
                .createdDate(imExBillEntity.getCreatedDate())
                .createdBy(imExBillEntity.getCreatedBy())
                .updatedDate(imExBillEntity.getUpdatedDate())
                .updatedBy(imExBillEntity.getUpdatedBy())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostApproveImportBillResponseBody postApproveImportBill(PostApproveImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postApproveImportBill requestBody {}", getClass().getSimpleName(), requestBody);

        if (StringUtils.equals(requestBody.getStatus(), StatusUtil.REFUSED.name()) && StringUtils.isEmpty(requestBody.getNote())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        ImExBillEntity imExBillEntity = imExBillRepository.findByCode(requestBody.getImportBillCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (!StringUtils.equals(imExBillEntity.getStatus(), StatusUtil.REVIEWING.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        UserInfoEntity userInfoEntity = userInfoRepository.findByUserId(requestBody.getUserId())
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        List<ApprovedDetailModel> approvedDetailModels;

        List<String> userIds = new ArrayList<>(Arrays.asList(imExBillEntity.getApproveDetail().split(",")));

        approvedDetailModels = gson.fromJson(imExBillEntity.getApprovedDetail(), new TypeToken<List<ApprovedDetailModel>>() {
        }.getType());
        List<ApprovedDetailModel> finalApprovedDetailModels = approvedDetailModels.stream().filter(item -> item.getStatus().equals(StatusUtil.APPROVED.name())).toList();
        userIds = userIds.stream().filter(e -> !finalApprovedDetailModels.stream().map(ApprovedDetailModel::getUserId).toList().contains(e)).toList();

        ApprovedDetailModel approvedDetailModel = ApprovedDetailModel.builder()
                .userId(userInfoEntity.getUserId())
                .userName(userInfoEntity.getFullName())
                .approveTime(serviceUtils.convertTimeStampToString(new Timestamp(System.currentTimeMillis())))
                .status(requestBody.getStatus())
                .note(requestBody.getNote())
                .build();
        approvedDetailModels.add(approvedDetailModel);
        if (!userIds.get(0).equals(requestBody.getUserId())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.BAD_REQUEST.getCode(), ExceptionTemplate.APPROVAL_INVALID.getMessage());
        }

        imExBillEntity.setApprovedDetail(gson.toJson(approvedDetailModels));
        imExBillEntity.setStatus(requestBody.getStatus().equals(StatusUtil.REFUSED.name()) ? StatusUtil.REFUSED.name() : approvedDetailModels.stream().filter(item -> item.getStatus().equals(StatusUtil.APPROVED.name())).toList().size() == new ArrayList<>(Arrays.asList(imExBillEntity.getApproveDetail().split(","))).size() ? StatusUtil.DONE.name() : imExBillEntity.getStatus());
        imExBillRepository.save(imExBillEntity);

        List<ImExDetailEntity> imExDetailEntities = imExDetailBillRepository.findAllByBillCode(imExBillEntity.getCode());

        List<WarehouseDetailEntity> warehouseDetails = imExDetailEntities.stream().map(e ->
                WarehouseDetailEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .warehouseCode(imExBillEntity.getWhCode())
                        .quantity(e.getRealQuantity())
                        .materialCode(e.getMaterialCode())
                        .status(StatusUtil.ACTIVE.name())
                        .createdBy(serviceUtils.getUserHeader(httpServletRequest))
                        .createdDate(new Timestamp(System.currentTimeMillis()))
                        .build()
        ).toList();
        wareHouseDetailRepository.saveAll(warehouseDetails);
        return PostApproveImportBillResponseBody.builder()
                .id(imExBillEntity.getId())
                .code(imExBillEntity.getCode())
                .approvedDetail(approvedDetailModels)
                .approveDetail(imExBillEntity.getApproveDetail())
                .status(imExBillEntity.getStatus())
                .build();
    }

    private Specification<ImExBillEntity> imExBillsSpec(PostListImportBillRequestBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            predicates.add(criteriaBuilder.equal(root.get("type"), "IM_BILL"));
            if (StringUtils.isNotEmpty(request.getStatus())) {
                predicates.add(criteriaBuilder.like(root.get("status"), "%" + request.getStatus() + "%"));
            }
            if (StringUtils.isNotEmpty(request.getSearchText())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + request.getSearchText().toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
