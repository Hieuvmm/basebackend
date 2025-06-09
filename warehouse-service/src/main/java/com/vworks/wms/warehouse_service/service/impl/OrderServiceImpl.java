package com.vworks.wms.warehouse_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.repository.JobPositionRepository;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.*;
import com.vworks.wms.warehouse_service.models.MaterialOrderModel;
import com.vworks.wms.warehouse_service.models.request.order.*;
import com.vworks.wms.warehouse_service.models.response.material.ParameterModel;
import com.vworks.wms.warehouse_service.models.response.order.PostDetailOrderResBody;
import com.vworks.wms.warehouse_service.repository.*;
import com.vworks.wms.warehouse_service.service.MaterialService;
import com.vworks.wms.warehouse_service.service.OrderService;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ObjectRepository objectRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final DetailMaterialsRepository detailMaterialsRepository;
    private final MaterialsRepository materialsRepository;
    private final DetailOrderRepository detailOrderRepository;
    private final ServiceUtils serviceUtils;
    private final UserInfoRepository userInfoRepository;
    private final WareHouseRepository wareHouseRepository;
    private final MaterialService materialService;
    private final UnitTypeRepository unitTypeRepository;
    private final JobPositionRepository jobPositionRepository;

    @Override
    public Page<OrderEntity> postListOrder(PostListOrderReqBody reqBody) {
        log.info("{} postListOrder reqBody {}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by("createdDate").descending());
        return orderRepository.findAll(orderSpec(reqBody), pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postCreateOrder(PostCreateOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateOrder reqBody {}", getClass().getSimpleName(), reqBody);

        checkValid(reqBody);

        long countOrder = orderRepository.count();

        List<DetailMaterialsEntity> detailMaterialsEntities = checkDetailMaterial(reqBody);

        List<MaterialsEntity> materialsEntities = materialsRepository.findAll();
        OrderEntity orderEntity = new OrderEntity();
        buildCreateOrder(orderEntity, reqBody);
        orderEntity.setId(UUID.randomUUID().toString());
        orderEntity.setCode("ORDER" + (countOrder + 1));
        orderEntity.setStatus(StatusUtil.CREATED.name());
        orderEntity.setCreatedBy(serviceUtils.getUserHeader(httpServletRequest));
        orderEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        orderEntity.setWhExport(reqBody.getWhExport());
        orderEntity.setPaidMethod(reqBody.getPaidMethod());
        if (StringUtils.isNotEmpty(reqBody.getAdvanceAmount())) {
            orderEntity.setAdvanceAmount(new BigDecimal(reqBody.getAdvanceAmount()));
        }

        if (StringUtils.isNotEmpty(reqBody.getAdvanceDate())) {
            orderEntity.setAdvanceDate(serviceUtils.convertStringToTimeStamp(reqBody.getAdvanceDate()));
        }

        if (StringUtils.isNotEmpty(reqBody.getDiscountRate())) {
            orderEntity.setDiscountRate(Integer.parseInt(reqBody.getDiscountRate()));
        }

        if (StringUtils.isNotEmpty(reqBody.getNote())) {
            orderEntity.setNote(reqBody.getNote());
        }

        if (StringUtils.isNotEmpty(reqBody.getTax())) {
            orderEntity.setTax(Integer.parseInt(reqBody.getTax()));
        }


        orderRepository.save(orderEntity);

        detailOrderRepository.saveAll(buildDetailOrderEntities(reqBody, detailMaterialsEntities, materialsEntities, orderEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateOrder(PostUpdateOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postListOrder reqBody {}", getClass().getSimpleName(), reqBody);
        OrderEntity orderEntity = orderRepository.findByCode(reqBody.getCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        checkValid(reqBody);

        List<DetailMaterialsEntity> detailMaterialsEntities = checkDetailMaterial(reqBody);

        List<MaterialsEntity> materialsEntities = materialsRepository.findAll();

        buildCreateOrder(orderEntity, reqBody);
        orderEntity.setStatus(StringUtils.isBlank(reqBody.getStatus()) ? StatusUtil.CREATED.name() : reqBody.getStatus());
        orderEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        orderEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        orderEntity.setWhExport(reqBody.getWhExport());
        orderEntity.setPaidMethod(reqBody.getPaidMethod());
        if (StringUtils.isNotEmpty(reqBody.getAdvanceAmount())) {
            orderEntity.setAdvanceAmount(new BigDecimal(reqBody.getAdvanceAmount()));
        }

        if (StringUtils.isNotEmpty(reqBody.getAdvanceDate())) {
            orderEntity.setAdvanceDate(serviceUtils.convertStringToTimeStamp(reqBody.getAdvanceDate()));
        }

        if (StringUtils.isNotEmpty(reqBody.getDiscountRate())) {
            orderEntity.setDiscountRate(Integer.parseInt(reqBody.getDiscountRate()));
        }

        if (StringUtils.isNotEmpty(reqBody.getNote())) {
            orderEntity.setNote(reqBody.getNote());
        }

        if (StringUtils.isNotEmpty(reqBody.getTax())) {
            orderEntity.setTax(Integer.parseInt(reqBody.getTax()));
        }
        orderRepository.save(orderEntity);

        List<DetailOrderEntity> detailOrderEntities = detailOrderRepository.findAllByOrderCode(orderEntity.getCode());
        detailOrderRepository.deleteAll(detailOrderEntities);

        detailOrderRepository.saveAll(buildDetailOrderEntities(reqBody, detailMaterialsEntities, materialsEntities, orderEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public PostDetailOrderResBody postDetailOrder(PostDetailOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postListOrder reqBody {}", getClass().getSimpleName(), reqBody);
        OrderEntity orderEntity = orderRepository.findByCode(reqBody.getCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        List<DetailOrderEntity> detailOrderEntities = detailOrderRepository.findAllByOrderCode(orderEntity.getCode());
        String username = StringUtils.isNotEmpty(httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD) : null;
        List<MaterialOrderModel> materialOrderModels = detailOrderEntities.stream().map(e ->
                {
                    Optional<DetailMaterialsEntity> detailMaterialsEntity = detailMaterialsRepository.findFirstByCode(e.getMaterialCode());
                    String unitCode = detailMaterialsEntity.map(DetailMaterialsEntity::getMeasureKeyword).orElse("");
                    String materialTypeCode = detailMaterialsEntity.map(DetailMaterialsEntity::getMaterialTypeCode).orElse("");
                    Optional<MaterialsEntity> materialsEntity = materialsRepository.findByCodeOrName(materialTypeCode, materialTypeCode);
                    return MaterialOrderModel.builder()
                            .code(e.getMaterialCode())
                            .name(e.getMaterialName())
                            .price(e.getPrice())
                            .quantity(e.getQuantity())
                            .priceDiscount(detailMaterialsEntity.map(DetailMaterialsEntity::getDiscount).orElse(""))
                            .unit(unitTypeRepository.findByCodeOrName(unitCode, unitCode).map(UnitTypeEntity::getName).orElse(""))
                            .materialType(materialsEntity.map(MaterialsEntity::getName).orElse(""))
                            .parameter(getParameter(detailMaterialsEntity.map(DetailMaterialsEntity::getParameters).orElse("")))
                            .discountMaterialModel(materialService.getDiscountModel(detailMaterialsEntity.map(DetailMaterialsEntity::getDiscount).orElse(""), username))
                            .build();
                }
        ).toList();
        return PostDetailOrderResBody.builder()
                .order(orderEntity)
                .materialOrders(materialOrderModels)
                .build();
    }

    private String getParameter(String e) {
        List<ParameterModel> parameterModelList = materialService.mapParameter(e);
        return parameterModelList.stream()
                .map(ParameterModel::getParameterValue)
                .collect(Collectors.joining("/ "));
    }

    private List<DetailOrderEntity> buildDetailOrderEntities(BaseOrderReqBody reqBody,
                                                             List<DetailMaterialsEntity> detailMaterialsEntities,
                                                             List<MaterialsEntity> materialsEntities,
                                                             OrderEntity orderEntity) {
        List<DetailOrderEntity> a = detailMaterialsEntities.stream().map(e ->
                DetailOrderEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .orderCode(orderEntity.getCode())
                        .materialCode(e.getCode())
                        .materialName(e.getName())
                        .materialTypeName(Objects.requireNonNull(materialsEntities.stream()
                                .filter(f -> f.getCode().equals(e.getMaterialTypeCode()))
                                .findFirst().orElse(null)).getName())
                        .materialTypeCode(e.getMaterialTypeCode())
                        .price(Objects.requireNonNull(reqBody.getMaterialOrders().stream()
                                .filter(f -> f.getCode().equals(e.getCode()))
                                .findFirst().orElse(null)).getPrice())
                        .quantity(Objects.requireNonNull(reqBody.getMaterialOrders().stream()
                                .filter(f -> f.getCode().equals(e.getCode()))
                                .findFirst().orElse(null)).getQuantity())
                        .status(StatusUtil.ACTIVE.name())
                        .build()
        ).collect(Collectors.toList());
        log.info("{} buildDetailOrderEntities countDetailOrderEntity{}", getClass().getSimpleName(), a.size());
        return a;
    }

    private void buildCreateOrder(OrderEntity orderEntity, BaseOrderReqBody reqBody) {
        orderEntity.setOrderType(reqBody.getOrderType());
        orderEntity.setCustomerCode(reqBody.getCustomerCode());
        orderEntity.setCustomerType(reqBody.getCustomerType());
        orderEntity.setDeliveryMethod(reqBody.getDeliveryMethod());
        orderEntity.setExchangeRateCode(reqBody.getExchangeRateCode());
        orderEntity.setTotal(calculateTotalOrder(reqBody.getMaterialOrders()));
    }


    private BigDecimal calculateTotalOrder(List<MaterialOrderModel> materialOrders) {
        return materialOrders.stream()
                .map(e -> e.getPrice().multiply(BigDecimal.valueOf(e.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkValid(BaseOrderReqBody reqBody) throws WarehouseMngtSystemException {
        objectRepository.findByCodeOrName(reqBody.getCustomerCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        exchangeRateRepository.findByCodeOrName(reqBody.getExchangeRateCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (CollectionUtils.isEmpty(reqBody.getMaterialOrders())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        Optional<WarehouseEntity> warehouseEntity = wareHouseRepository.findByCode(reqBody.getWhExport());

        if (warehouseEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.WH_CODE_NOT_FOUND.getCode(), ExceptionTemplate.WH_CODE_NOT_FOUND.getMessage());
        }
        for (MaterialOrderModel x : reqBody.getMaterialOrders()) {
            if (x.getQuantity() <= 0) {
                throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
            }
        }
    }

    private List<DetailMaterialsEntity> checkDetailMaterial(BaseOrderReqBody reqBody) throws WarehouseMngtSystemException {
        List<String> codeMaterials = reqBody.getMaterialOrders().stream().map(MaterialOrderModel::getCode).collect(Collectors.toList());

        List<DetailMaterialsEntity> detailMaterialsEntities = detailMaterialsRepository.findAllByCodeIn(codeMaterials);

        if (codeMaterials.size() != detailMaterialsEntities.size()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return detailMaterialsEntities;
    }

    private Specification<OrderEntity> orderSpec(PostListOrderReqBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotBlank(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            String valueSearchText = "%" + request.getSearchText() + "%";
            if (StringUtils.isNotEmpty(request.getSearchText())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), valueSearchText.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public BaseResponse<?> postUpdateStatusOrder(PostUpdateStatusOrderRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postUpdateStatusOrder with requestBody = {}", this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        OrderEntity orderEntity = orderRepository.findByCode(requestBody.getCode()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        orderEntity.setStatus(requestBody.getStatus());
        orderEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));
        orderEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(orderEntity);
        return new BaseResponse<>();
    }

    @Override
    public BaseResponse<?> postAssignApprovalOrder(PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postAssignApprovalOrder requestBody {}", getClass().getSimpleName(), requestBody);

        if (CollectionUtils.isEmpty(requestBody.getOrderCodeList()) || CollectionUtils.isEmpty(requestBody.getApproves()) || CollectionUtils.isEmpty(requestBody.getFollows())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        List<OrderEntity> orderEntityList = orderRepository.findAllByCodeIn(requestBody.getOrderCodeList());

        boolean checkStatus = orderEntityList.stream().anyMatch(e -> StringUtils.equals(e.getStatus(), StatusUtil.CREATED.name()));
        if (!checkStatus) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }

        if (orderEntityList.size() != requestBody.getOrderCodeList().size()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        if (!Collections.disjoint(requestBody.getApproves(), requestBody.getFollows())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REQUEST_INVALID.getCode(), ExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        List<String> userIds = new ArrayList<>();
        userIds.addAll(requestBody.getApproves());
        userIds.addAll(requestBody.getFollows());
        boolean checkExist = userIds.stream().allMatch(userInfoRepository::existsByUserCode);
        if (!checkExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        String userId = StringUtils.isNotEmpty(httpServletRequest.getHeader(com.vworks.wms.common_lib.utils.Commons.FIELD_USER_ID)) ? httpServletRequest.getHeader(com.vworks.wms.common_lib.utils.Commons.FIELD_USER_ID) : null;
        UserInfoEntity userInfo = userInfoRepository.findByUserId(userId).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        List<OrderEntity> orderListAssigns = orderEntityList.stream().peek(e -> {
            e.setApprovalBy(String.join(",", requestBody.getApproves()));
            e.setFollowBy(String.join(",", requestBody.getFollows()));
            e.setStatus(StatusUtil.REVIEWING.name());
            e.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            e.setUpdatedBy(!StringUtils.isBlank(httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD) : null);
        }).toList();

        orderRepository.saveAll(orderListAssigns);
        return new BaseResponse<>();
    }

    @Override
    public BaseResponse<?> postApprovedOrder(PostApprovedOrderRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} approval ExBill with request body orderCode = {}",
                this.getClass().getSimpleName(), requestBody.getOrderCode());
        if (StringUtils.isEmpty(requestBody.getOrderCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.INPUT_EMPTY.getCode(), ExceptionTemplate.INPUT_EMPTY.getMessage());
        }
        Optional<OrderEntity> orderEntity = orderRepository.findFirstByCodeAndStatus(requestBody.getOrderCode(), StatusUtil.REVIEWING.name());

        if (orderEntity.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.EX_CODE_NOT_FOUND.getMessage());
        }

        String username = StringUtils.isBlank(httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD)) ? "" : httpServletRequest.getHeader(com.vworks.wms.warehouse_service.utils.Commons.USER_CODE_FIELD);
        username = userInfoRepository.findFirstByUserCodeOrUserId(username, username).map(UserInfoEntity::getUserCode).orElse("");
        if (StringUtils.isEmpty(orderEntity.get().getApprovalBy())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.APPROVAL_EMPTY.getCode(), ExceptionTemplate.APPROVAL_EMPTY.getMessage());
        }

        List<String> approvalList = new ArrayList<String>(Arrays.asList(orderEntity.get().getApprovalBy().split(",")));
        if (!approvalList.contains(username)) {
            log.info("{} approvalExBill check user approval with username = {}, approvalList = {} ", this.getClass().getSimpleName(), username, approvalList);
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.USER_NOT_APPROVAL.getCode(), ExceptionTemplate.USER_NOT_APPROVAL.getMessage());
        }

        orderEntity.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        orderEntity.get().setUpdatedBy(username);
        orderEntity.get().setApprovedBy(username);
        orderEntity.get().setStatus(requestBody.getStatus());
        if (requestBody.getStatus().equalsIgnoreCase(com.vworks.wms.warehouse_service.utils.Commons.APPROVAL)) {
            handelApprovalOrder(orderEntity.get(), username);
        }

        if (requestBody.getStatus().equalsIgnoreCase(com.vworks.wms.warehouse_service.utils.Commons.REJECT)) {
            handelRejectOrder(orderEntity.get(), requestBody, username);
        }
        orderRepository.save(orderEntity.get());
        return new BaseResponse(StatusUtil.SUCCESS.name());
    }

    private void handelApprovalOrder(OrderEntity orderEntity, String username) {
        orderEntity.setStatus(StatusUtil.DONE.name());
        if (orderEntity.getOrderType().equals(Commons.ORDER_TYPE_ESTIMATE)) {
            orderEntity.setOrderType(Commons.ORDER_TYPE_SELL);
            orderEntity.setStatus(StatusUtil.CREATED.name());
        }
        orderEntity.setApprovedBy(username);
    }

    private void handelRejectOrder(OrderEntity orderEntity, PostApprovedOrderRequestBody requestBody, String username) throws WarehouseMngtSystemException {
        if (StringUtils.isEmpty(requestBody.getReason())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.REASON_EMPTY.getCode(), ExceptionTemplate.REASON_EMPTY.getMessage());
        }
        orderEntity.setCancelBy(username);
        orderEntity.setReason(requestBody.getReason());
        orderEntity.setStatus(StatusUtil.REFUSED.name());
    }
}
