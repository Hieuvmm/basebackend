package com.vworks.wms.warehouse_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.DateTimeFormatUtil;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.*;
import com.vworks.wms.warehouse_service.models.*;
import com.vworks.wms.warehouse_service.models.request.SearchExBillRequestBody;
import com.vworks.wms.warehouse_service.models.request.exportBill.*;
import com.vworks.wms.warehouse_service.models.response.SearchExBillResponseBody;
import com.vworks.wms.warehouse_service.models.response.exportBill.PostGetExportBillDetailResponseBody;
import com.vworks.wms.warehouse_service.repository.*;
import com.vworks.wms.warehouse_service.service.ExportBillService;
import com.vworks.wms.warehouse_service.utils.Commons;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportBillServiceImpl implements ExportBillService {
    private final ImExBillRepository imExBillRepository;
    private final WareHouseRepository wareHouseRepository;
    private final ImExDetailBillRepository imExDetailBillRepository;
    private final DetailMaterialsRepository detailMaterialsRepository;
    private final MaterialsRepository materialsRepository;
    private final ServiceUtils serviceUtils;
    private final ProviderRepository providerRepository;
    private final WareHouseDetailRepository wareHouseDetailRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final UserInfoRepository userInfoRepository;
    private final Gson gson;

    @Override
    public BaseResponse createExBill(CreateExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} createExBill with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getOrderNumber())
                || StringUtils.isEmpty(requestBody.getExCode())
                || StringUtils.isEmpty(requestBody.getDateBill())
                || StringUtils.isEmpty(requestBody.getCustomer())
                || StringUtils.isEmpty(requestBody.getDateEx())
                || StringUtils.isEmpty(requestBody.getDesc())
                || StringUtils.isEmpty(requestBody.getWhCode())
                || StringUtils.isEmpty(requestBody.getDestination())
                || requestBody.getProductEx().isEmpty()
                || requestBody.getApprovalBy().isEmpty()
                || requestBody.getFollowBy().isEmpty()
        ) {
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        Optional<ImExBillEntity> imExBillEntity = imExBillRepository.findFirstByCode(requestBody.getExCode());

        if (imExBillEntity.isPresent()) {
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.EX_CODE_EXIST.getCode(), ExceptionTemplate.EX_CODE_EXIST.getMessage());
        }

        checkWh(requestBody.getWhCode(), requestBody.getDestination());

        ImExBillEntity imExBillEntity1 = new ImExBillEntity();
        imExBillEntity1.setId(UUID.randomUUID().toString());
        imExBillEntity1.setCode(requestBody.getExCode());
        imExBillEntity1.setName(requestBody.getDesc());
        imExBillEntity1.setOrderCode(requestBody.getOrderNumber());
        imExBillEntity1.setOrderDate(requestBody.getDateBill());
        imExBillEntity1.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillEntity1.setCreatedBy(!StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
//        imExBillEntity1.setTransType(requestBody.getTypeEx());
        imExBillEntity1.setDestinationWh(requestBody.getDestination());
        imExBillEntity1.setType("EX");
        imExBillEntity1.setTotalPrice(requestBody.getTotalMoney() != null ? new BigDecimal(requestBody.getTotalMoney()) : null);
        imExBillEntity1.setStatus(StatusUtil.NEW.name());
        imExBillEntity1.setDescription(requestBody.getDesc());
        imExBillEntity1.setWhCode(requestBody.getWhCode());
        imExBillEntity1.setApproveDetail(String.join(", ", requestBody.getApprovalBy()));
        imExBillEntity1.setFollowDetail(String.join(", ", requestBody.getFollowBy()));
        imExBillEntity1.setExchangeRateCode(requestBody.getCcy());
        imExBillEntity1.setDateEx(requestBody.getDateEx());
        imExBillEntity1.setCustomer(requestBody.getCustomer());

        for (ProductExModel e : requestBody.getProductEx()) {
            for (MaterialsExModel x : e.getMaterialsEx()) {
                ImExDetailEntity imExDetailEntity = new ImExDetailEntity();
                imExDetailEntity.setId(UUID.randomUUID().toString());
                imExDetailEntity.setBillCode(imExBillEntity1.getCode());
                if (StringUtils.isEmpty(x.getMaterialCode())) {
                    throw new WarehouseMngtSystemException(404, ExceptionTemplate.MATERIAL_CODE_EMPTY.getCode(), ExceptionTemplate.MATERIAL_CODE_EMPTY.getMessage());
                }
                imExDetailEntity.setMaterialCode(x.getMaterialCode());

                if (StringUtils.isEmpty(x.getRealQuantity()) || StringUtils.isEmpty(x.getExpQuantity())) {
                    throw new WarehouseMngtSystemException(404, ExceptionTemplate.QUANTITY_EMPTY.getCode(), ExceptionTemplate.QUANTITY_EMPTY.getMessage());
                }


                imExDetailEntity.setTimes(Integer.valueOf(e.getTime()));
                imExDetailEntity.setCreatedBy(!StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
                imExDetailEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                imExDetailEntity.setPrice(BigDecimal.valueOf(Long.parseLong(x.getTotalPrice())));
                imExDetailEntity.setExpectedQuantity(Integer.parseInt(x.getExpQuantity()));
                imExDetailEntity.setRealQuantity(Integer.parseInt(x.getRealQuantity()));
                imExDetailEntity.setStatus(StatusUtil.CREATED.name());

                imExDetailBillRepository.save(imExDetailEntity);
            }
        }

        imExBillRepository.save(imExBillEntity1);
        return new BaseResponse(StatusUtil.SUCCESS.name());
    }

    private void checkWh(String wh, String destination) throws WarehouseMngtSystemException {
        if (wh.equalsIgnoreCase(destination)) {
            log.info("{} checkWh whCode {} with destination whCode {} is same", this.getClass().getSimpleName(), wh, destination);
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.WH_SAME.getCode(), ExceptionTemplate.WH_SAME.getMessage());
        }
        WarehouseEntity warehouseEntity = wareHouseRepository.findFirstByCode(wh);
        if (Objects.isNull(warehouseEntity)) {
            log.info("{} checkWh data not found with whCode = {}", this.getClass().getSimpleName(), wh);
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.WH_CODE_NOT_FOUND.getCode(), ExceptionTemplate.WH_CODE_NOT_FOUND.getMessage());
        }

        WarehouseEntity warehouseDestination = wareHouseRepository.findFirstByCode(destination);
        if (Objects.isNull(warehouseDestination)) {
            log.info("{} checkWh data not found warehouse destination with whCode = {}", this.getClass().getSimpleName(), wh);
            throw new WarehouseMngtSystemException(404, ExceptionTemplate.WH_CODE_NOT_FOUND.getCode(), ExceptionTemplate.WH_CODE_NOT_FOUND.getMessage());
        }
    }


    @Override
    public BaseResponse searchExBill(SearchExBillRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} searchExBill with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        SearchExBillResponseBody responseBody = new SearchExBillResponseBody();
        List<ImExBillEntity> imExBillEntityList = new ArrayList<>();
        Pageable pageAble = serviceUtils.pageAble(requestBody.getPageNumber(), requestBody.getPageSize());
        if (StringUtils.isEmpty(requestBody.getSearchText()) && StringUtils.isEmpty(requestBody.getStatus())) {
            Page imExBillEntityPage = imExBillRepository.findAll(pageAble);
            imExBillEntityList = imExBillEntityPage.getContent();
            responseBody.setTotalPage(imExBillEntityPage.getTotalPages());
        } else if (StringUtils.isEmpty(requestBody.getSearchText())) {
            Page imExBillEntityPage = imExBillRepository.findAllByStatus(pageAble, requestBody.getStatus());
            imExBillEntityList = imExBillEntityPage.getContent();
            responseBody.setTotalPage(imExBillEntityPage.getTotalPages());
        } else {
            Page imExBillEntityPage = imExBillRepository.findFirstByCode(pageAble, requestBody.getSearchText());
            imExBillEntityList = imExBillEntityPage.getContent();
            responseBody.setTotalPage(imExBillEntityPage.getTotalPages());
        }

        List<SearchExBillModel> exBillModels = new ArrayList<>();
        if (!imExBillEntityList.isEmpty()) {
            imExBillEntityList.stream().map(x -> {
                SearchExBillModel model = new SearchExBillModel();
                model.setCustomer("KH01");
                model.setCreatedBy(x.getCreatedBy());
                model.setCreatedDate(String.valueOf(x.getCreatedDate()));
                model.setStatus(x.getStatus());
                model.setExCode(x.getCode());
                WarehouseEntity warehouseEntity = wareHouseRepository.findFirstByCode(x.getWhCode());
                if (!Objects.isNull(warehouseEntity) && StringUtils.isNotEmpty(warehouseEntity.getName())) {
                    model.setWareHouse(warehouseEntity.getName());
                }
                model.setTotalPrice(String.valueOf(x.getTotalPrice()));
                model.setStatusPayment("TT");
                return exBillModels.add(model);
            }).collect(Collectors.toList());
        }
        responseBody.setExBillList(exBillModels);

        log.info("[END] {} searchExBill with imExBillEntities = {}",
                this.getClass().getSimpleName(), new Gson().toJson(imExBillEntityList));
        return new BaseResponse(responseBody);
    }

    @Override
    public BaseResponse approvalExBill(PostApprovalExBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} approval ExBill with request body exCode = {}",
                this.getClass().getSimpleName(), requestBody.getExCode());
        if (StringUtils.isEmpty(requestBody.getExCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }
        Optional<ImExBillEntity> imExBillEntity = imExBillRepository.findFirstByCodeAndStatus(requestBody.getExCode(), StatusUtil.CREATED.name());

        if (imExBillEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.EX_CODE_NOT_FOUND.getCode(), ExceptionTemplate.EX_CODE_NOT_FOUND.getMessage());
        }

        if (imExBillEntity.get().getStatus().equals(Commons.INACTIVE)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.EX_BILL_INACTIVE.getCode(), ExceptionTemplate.EX_BILL_INACTIVE.getMessage());
        }
        String username = StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? "" : httpServletRequest.getHeader(Commons.USER_CODE_FIELD);

        if (StringUtils.isEmpty(imExBillEntity.get().getApproveDetail())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.APPROVAL_EMPTY.getCode(), ExceptionTemplate.APPROVAL_EMPTY.getMessage());
        }

        List<String> approvalList = new ArrayList<String>(Arrays.asList(imExBillEntity.get().getApproveDetail().split(",")));
        if (!approvalList.contains(username)) {
            log.info("{} approvalExBill check user approval with username = {}, approvalList = {} ", this.getClass().getSimpleName(), username, approvalList);
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.USER_NOT_APPROVAL.getCode(), ExceptionTemplate.USER_NOT_APPROVAL.getMessage());
        }

        imExBillEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillEntity.get().setUpdatedBy(username);
        imExBillEntity.get().setApprovedDetail(username);
        imExBillEntity.get().setStatus(requestBody.getStatus());
        if (requestBody.getStatus().equalsIgnoreCase(Commons.APPROVAL)) {
            imExBillEntity.get().setApprovedDetail(username);
            handelQuantityWarehouse(requestBody, imExBillEntity.get(), username);
        }

        if (requestBody.getStatus().equalsIgnoreCase(Commons.REJECT)) {
            if (StringUtils.isEmpty(requestBody.getReason())) {
                throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REASON_EMPTY.getCode(), ExceptionTemplate.REASON_EMPTY.getMessage());
            }
            imExBillEntity.get().setCancelBy(username);
            imExBillEntity.get().setReason(requestBody.getReason());
        }
        imExBillRepository.save(imExBillEntity.get());

        return new BaseResponse(StatusUtil.SUCCESS.name());
    }

    private void handelQuantityWarehouse(PostApprovalExBillRequestBody requestBody, ImExBillEntity imExBillEntity, String username) throws WarehouseMngtSystemException {
        List<ImExDetailEntity> imExDetailEntityList = imExDetailBillRepository.findAllByBillCode(requestBody.getExCode());
        List<WarehouseDetailEntity> warehouseDetailEntityList = new ArrayList<>();
        if (!imExDetailEntityList.isEmpty()) {
            for (ImExDetailEntity x : imExDetailEntityList) {
                Optional<WarehouseDetailEntity> warehouseDetailEntity = wareHouseDetailRepository.findAllByWarehouseCodeAndMaterialCode(imExBillEntity.getWhCode(), x.getMaterialCode());
                if (warehouseDetailEntity.isEmpty()) {
                    throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
                }
                int quantityRemaining = warehouseDetailEntity.get().getQuantity() - x.getRealQuantity();
                if (quantityRemaining < 0) {
                    throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.QUANTITY_INVALID.getCode(), ExceptionTemplate.QUANTITY_INVALID.getMessage());
                }
                warehouseDetailEntity.get().setQuantity(quantityRemaining);
                warehouseDetailEntity.get().setUpdatedBy(username);
                warehouseDetailEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                warehouseDetailEntityList.add(warehouseDetailEntity.get());
                warehouseDetailEntityList.add(addMaterialToWarehouse(x, username, imExBillEntity.getWhCode()));
            }

            wareHouseDetailRepository.saveAll(warehouseDetailEntityList);
        }
    }

    private WarehouseDetailEntity addMaterialToWarehouse(ImExDetailEntity imExDetailEntity, String username, String whCode) {
        Optional<WarehouseDetailEntity> warehouseDetailEntity = wareHouseDetailRepository.findAllByWarehouseCodeAndMaterialCode(whCode, imExDetailEntity.getMaterialCode());
        if (warehouseDetailEntity.isEmpty()) {
            return WarehouseDetailEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .warehouseCode(whCode)
                    .materialCode(imExDetailEntity.getMaterialCode())
                    .quantity(imExDetailEntity.getRealQuantity())
                    .status(StatusUtil.ACTIVE.name())
                    .createdBy(username)
                    .createdDate(new Timestamp(System.currentTimeMillis()))
                    .build();
        } else {
            warehouseDetailEntity.get().setQuantity(warehouseDetailEntity.get().getQuantity() + imExDetailEntity.getRealQuantity());
            warehouseDetailEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            warehouseDetailEntity.get().setUpdatedBy(username);
            return warehouseDetailEntity.get();
        }
    }

    @Override
    public PostGetExportBillDetailResponseBody deTailExBill(PostGetDetailExportBillRequestBody requestBody) throws WarehouseMngtSystemException {
        log.info("[START] {} detailExBill with export bill code = {}",
                this.getClass().getSimpleName(), requestBody.getExCode());
        if (StringUtils.isEmpty(requestBody.getExCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        Optional<ImExBillEntity> imExBillEntity = imExBillRepository.findFirstByCode(requestBody.getExCode());

        if (imExBillEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.EX_CODE_NOT_FOUND.getCode(), ExceptionTemplate.EX_CODE_NOT_FOUND.getMessage());
        }

        PostGetExportBillDetailResponseBody responseBody = new PostGetExportBillDetailResponseBody();
        responseBody.setExCode(responseBody.getExCode());
        responseBody.setTypeEx(imExBillEntity.get().getTransType());
        responseBody.setOrderNumber(imExBillEntity.get().getOrderCode());
        responseBody.setDateBill(String.valueOf(imExBillEntity.get().getOrderDate()));
        responseBody.setDesc(imExBillEntity.get().getDescription());
        responseBody.setDateEx(imExBillEntity.get().getDateEx());
        responseBody.setDestination(imExBillEntity.get().getDestinationWh());
        responseBody.setStatus(imExBillEntity.get().getStatus());
        responseBody.setCcy(StringUtils.isEmpty(imExBillEntity.get().getExchangeRateCode()) ? "VND" : imExBillEntity.get().getExchangeRateCode());
        responseBody.setWhCode(imExBillEntity.get().getWhCode());
        responseBody.setCustomer(imExBillEntity.get().getCustomer());
        responseBody.setApprovalBy(
                StringUtils.isEmpty(imExBillEntity.get().getApproveDetail()) ? null : Arrays.asList(imExBillEntity.get().getApproveDetail().split(","))
        );
        responseBody.setFollowBy(
                StringUtils.isEmpty(imExBillEntity.get().getFollowDetail()) ? null : Arrays.asList(imExBillEntity.get().getFollowDetail().split(","))
        );
        responseBody.setTotalMoney(String.valueOf(imExBillEntity.get().getTotalPrice()));
        responseBody.setExCode(imExBillEntity.get().getCode());
        responseBody.setDesc(imExBillEntity.get().getDescription());
        WarehouseEntity warehouseEntity = wareHouseRepository.findFirstByCode(imExBillEntity.get().getWhCode());
        if (!Objects.isNull(warehouseEntity)) {
            responseBody.setWhName(warehouseEntity.getName());
        }

        List<ImExDetailEntity> imExDetailEntityList = imExDetailBillRepository.findAllByBillCodeAndStatus(imExBillEntity.get().getCode(), StatusUtil.CREATED.name());

        List<Integer> listTime = new ArrayList<>();
        for (ImExDetailEntity x : imExDetailEntityList) {
            if (!listTime.contains(x.getTimes())) {
                listTime.add(x.getTimes());
            }
        }
        List<GetDetailProductModel> productEx = new ArrayList<>();

        for (Integer e : listTime) {
            GetDetailProductModel getDetailProductModel = new GetDetailProductModel();
            getDetailProductModel.setTime(String.valueOf(e));
            List<GetDetailMaterialExModel> materialsEx = new ArrayList<>();
            for (ImExDetailEntity x : imExDetailEntityList) {
                GetDetailMaterialExModel getDetailMaterialExModel = new GetDetailMaterialExModel();
                if (Objects.equals(x.getTimes(), e)) {
                    getDetailMaterialExModel.setMaterialCode(x.getMaterialCode());
                    getDetailMaterialExModel.setExpQuantity(String.valueOf(x.getExpectedQuantity()));
                    getDetailMaterialExModel.setRealQuantity(String.valueOf(x.getRealQuantity()));
                    getDetailMaterialExModel.setTotalPrice(String.valueOf(x.getPrice()));


                    Optional<DetailMaterialsEntity> detailMaterialsEntity = detailMaterialsRepository.findFirstByCode(x.getMaterialCode());

                    if (detailMaterialsEntity.isPresent()) {
                        getDetailMaterialExModel.setMaterialName(detailMaterialsEntity.get().getName());
                        getDetailMaterialExModel.setUnit(unitTypeRepository.findByCodeOrName(detailMaterialsEntity.get().getMeasureKeyword(), detailMaterialsEntity.get().getMeasureKeyword()).map(UnitTypeEntity::getName).orElse(""));
//                        getDetailMaterialExModel.setParameter(detailMaterialsEntity.get().getParameter());
                        getDetailMaterialExModel.setPrice(String.valueOf(detailMaterialsEntity.get().getListPrice()));
                        Optional<MaterialsEntity> materialsEntity = materialsRepository.findByCodeOrName(detailMaterialsEntity.get().getMaterialTypeCode(), null);
                        materialsEntity.ifPresent(entity -> getDetailMaterialExModel.setMaterialType(entity.getName()));
                    }
                    materialsEx.add(getDetailMaterialExModel);
                }
            }
            if (!materialsEx.isEmpty()) {
                getDetailProductModel.setMaterialsEx(materialsEx);
            }
            productEx.add(getDetailProductModel);
        }
        responseBody.setProductEx(productEx);

        return responseBody;
    }


    @Override
    public BaseResponse updateExBill(PostUpdateExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} updateExBill with request {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getExCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }

        Optional<ImExBillEntity> imExBillEntity = imExBillRepository.findFirstByCode(requestBody.getExCode());

        if (imExBillEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.EX_CODE_NOT_FOUND.getCode(), ExceptionTemplate.EX_CODE_NOT_FOUND.getMessage());
        }

        String destinationWh = StringUtils.isNotEmpty(requestBody.getDestination()) ? requestBody.getDestination() : imExBillEntity.get().getDestinationWh();
        String wh = StringUtils.isNotEmpty(requestBody.getWhCode()) ? requestBody.getWhCode() : imExBillEntity.get().getWhCode();
        checkWh(wh, destinationWh);

        imExBillEntity.get().setDestinationWh(destinationWh);
        imExBillEntity.get().setOrderCode(StringUtils.isNotEmpty(requestBody.getOrderNumber()) ? requestBody.getOrderNumber() : imExBillEntity.get().getOrderCode());
        imExBillEntity.get().setOrderDate(StringUtils.isNotEmpty(requestBody.getDateBill()) ? requestBody.getDateBill() : imExBillEntity.get().getOrderDate());
        imExBillEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillEntity.get().setDateEx(requestBody.getDateEx());
        imExBillEntity.get().setDescription(StringUtils.isNotEmpty(requestBody.getDesc()) ? requestBody.getDesc() : imExBillEntity.get().getDescription());
        imExBillEntity.get().setWhCode(wh);
        imExBillEntity.get().setTotalPrice(StringUtils.isNotEmpty(requestBody.getTotalMoney()) ? new BigDecimal(requestBody.getTotalMoney()) : imExBillEntity.get().getTotalPrice());
        imExBillEntity.get().setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? "" : httpServletRequest.getHeader(Commons.USER_CODE_FIELD));
        imExBillEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillEntity.get().setExchangeRateCode(StringUtils.isEmpty(requestBody.getCcy()) ? imExBillEntity.get().getExchangeRateCode() : requestBody.getCcy());
        imExBillEntity.get().setCustomer(requestBody.getCustomer());
        imExBillEntity.get().setApproveDetail((requestBody.getApprovalBy() != null && requestBody.getApprovalBy().isEmpty()) ? imExBillEntity.get().getApprovedDetail() : String.join(", ", requestBody.getApprovalBy()));
        imExBillEntity.get().setFollowDetail((requestBody.getFollowBy() != null
                && requestBody.getFollowBy().isEmpty()) ? imExBillEntity.get().getFollowDetail() : String.join(", ", requestBody.getFollowBy()));

        List<ImExDetailEntity> imExDetailEntityList = new ArrayList<>();
        if (Objects.nonNull(requestBody.getProductEx())) {

            for (ProductExModel e : requestBody.getProductEx()) {
                if (StringUtils.isEmpty(e.getTime())) {
                    throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.TIME_EMPTY.getCode(), ExceptionTemplate.TIME_EMPTY.getMessage());
                }

                for (MaterialsExModel x : e.getMaterialsEx()) {
                    ImExDetailEntity imExDetailEntity = new ImExDetailEntity();
                    imExDetailEntity.setId(UUID.randomUUID().toString());
                    imExDetailEntity.setTimes(Integer.parseInt(e.getTime()));
                    imExDetailEntity.setBillCode(requestBody.getExCode());
                    imExDetailEntity.setMaterialCode(x.getMaterialCode());
                    imExDetailEntity.setExpectedQuantity(Integer.parseInt(x.getExpQuantity()));
                    imExDetailEntity.setPrice(BigDecimal.valueOf(Long.parseLong(x.getTotalPrice())));
                    imExDetailEntity.setRealQuantity(Integer.parseInt(x.getRealQuantity()));
                    imExDetailEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                    imExDetailEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? "haitd" : httpServletRequest.getHeader(Commons.USER_CODE_FIELD));
                    imExDetailEntity.setStatus(StatusUtil.CREATED.name());
                    imExDetailEntityList.add(imExDetailEntity);
                }
            }
        }

        List<ImExDetailEntity> imExDetailEntityListCancel = imExDetailBillRepository.findAllByBillCodeAndStatus(requestBody.getExCode(), StatusUtil.CREATED.name());

        if (!imExDetailEntityListCancel.isEmpty()) {
            imExDetailEntityListCancel.forEach(x -> x.setStatus(StatusUtil.CANCEL.name()));
            imExDetailBillRepository.saveAll(imExDetailEntityListCancel);
        }

        imExBillRepository.save(imExBillEntity.get());
        if (!imExDetailEntityList.isEmpty()) {
            imExDetailBillRepository.saveAll(imExDetailEntityList);
        }
        return new BaseResponse(StatusUtil.SUCCESS.name());
    }

    @Override
    public SearchExBillResponseBody searchExBillV2(SearchExBillRequestBody requestBody) {
        log.info("[START] {} searchExBillV2 with request body = {}", this.getClass().getSimpleName(), requestBody);


        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit());
        Page<ImExBillEntity> imExBillEntities = imExBillRepository.findAll(specificationExBill(requestBody), pageable);

        List<SearchExBillModel> searchExBillModels = imExBillEntities.getContent().stream().map(x -> {
            return SearchExBillModel.builder()
                    .exCode(x.getCode())
                    .name(x.getName())
                    .status(x.getStatus())
                    .wareHouse(wareHouseRepository.findByCode(x.getWhCode()).map(WarehouseEntity::getName).orElse(""))
                    .totalPrice(String.valueOf(x.getTotalPrice()))
                    .provider(providerRepository.findFirstByCode(x.getProviderCode()).map(ProviderEntity::getName).orElse(""))
                    .createdBy(x.getCreatedBy())
                    .createdDate(serviceUtils.convertTimeStampToStringWithFormatDate(x.getCreatedDate(), DateTimeFormatUtil.DD_MM_YYYY_1.getValue()))
                    .build();
        }).toList();

        return SearchExBillResponseBody.builder()
                .exBillList(searchExBillModels)
                .totalElement(imExBillEntities.getTotalElements())
                .totalPage(imExBillEntities.getTotalPages())
                .build();
    }

    private Specification<ImExBillEntity> specificationExBill(SearchExBillRequestBody requestBody) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("type"), Commons.TYPE_EX));
            predicates.add(criteriaBuilder.notEqual(root.get("status"), Commons.DELETED));

            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), requestBody.getStatus()));
            }
            String keyword = "%" + requestBody.getSearchText() + "%";
            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                Predicate code = criteriaBuilder.equal(root.get("code"), requestBody.getSearchText());
                Predicate name = criteriaBuilder.like(root.get("name"), keyword);
                predicates.add(criteriaBuilder.or(code, name));
//                predicates.add(code);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public BaseResponse<?> deleteExBill(PostDeleteExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} deteleExBill with exCode = {}", this.getClass().getSimpleName(), requestBody.getExCode());
        if (StringUtils.isEmpty(requestBody.getExCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.TIME_EMPTY.getMessage());
        }

        Optional<ImExBillEntity> imExBillEntity = imExBillRepository.findFirstByCode(requestBody.getExCode());

        if (imExBillEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.EX_CODE_NOT_FOUND.getCode(), ExceptionTemplate.EX_CODE_NOT_FOUND.getMessage());
        }

        imExBillEntity.get().setStatus(Commons.DELETED);
        imExBillEntity.get().setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? "haitd" : httpServletRequest.getHeader(Commons.USER_CODE_FIELD));
        imExBillEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        imExBillRepository.save(imExBillEntity.get());
        return new BaseResponse<>();
    }

    @Override
    public BaseResponse<?> assignAproval(PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} assignAproval requestBody {}", getClass().getSimpleName(), requestBody);

        if (CollectionUtils.isEmpty(requestBody.getExportBillCodes()) || CollectionUtils.isEmpty(requestBody.getApproves()) || CollectionUtils.isEmpty(requestBody.getFollows())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        List<ImExBillEntity> imExBills = imExBillRepository.findAllByCodeIn(requestBody.getExportBillCodes());

        boolean checkStatus = imExBills.stream().anyMatch(e -> StringUtils.equals(e.getStatus(), StatusUtil.NEW.name()));
        if (checkStatus) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }

        if (imExBills.size() != requestBody.getExportBillCodes().size()) {
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
        String userId = StringUtils.isNotEmpty(httpServletRequest.getHeader(com.vworks.wms.common_lib.utils.Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(com.vworks.wms.common_lib.utils.Commons.FIELD_USER_ID) : null;
        UserInfoEntity userInfo = userInfoRepository.findByUserId(userId).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        List<ImExBillEntity> imExBillAssigns = imExBills.stream().peek(e -> {
            e.setApproveDetail(String.join(",", requestBody.getApproves()));
            e.setFollowDetail(String.join(",", requestBody.getFollows()));
            e.setStatus(StatusUtil.CREATED.name());
            e.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            e.setUpdatedBy(!StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        }).toList();

        imExBillRepository.saveAll(imExBillAssigns);
        return new BaseResponse<>();
    }
}
