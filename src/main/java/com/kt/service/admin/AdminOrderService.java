package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.AdminOrderResponse;

public interface AdminOrderService {

	Page<AdminOrderResponse.Search> searchOrder(Pageable pageable);

	AdminOrderResponse.Detail getOrderDetail(UUID orderId);

	void forceChangeStatus(UUID orderProductId, OrderProductStatus status);

}
