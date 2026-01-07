package com.kt.service.seller.order;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.SellerOrderResponse;

public interface SellerOrderService {

	void cancelOrderProduct(UUID orderProductId, UUID sellerId);

	void confirmPaidOrderProduct(UUID orderProductId, UUID sellerId);

	Page<SellerOrderResponse.Search> searchOrderProducts(Pageable pageable, OrderProductStatus status,
		UUID orderProductId, UUID sellerID);

}
