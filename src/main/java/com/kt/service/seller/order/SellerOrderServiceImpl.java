package com.kt.service.seller.order;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.SellerOrderResponse;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.util.Preconditions;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerOrderServiceImpl implements SellerOrderService {

	private final SellerRepository sellerRepository;
	private final OrderProductRepository orderProductRepository;
	private final InventoryRepository inventoryRepository;

	@Override
	public void cancelOrderProduct(UUID orderProductId, UUID sellerId) {
		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);
		Preconditions.validate(orderProduct.getProduct().getSeller().getId() == sellerId,
			ErrorCode.ORDER_PRODUCT_NOT_OWNER);

		OrderProductStatus status = orderProduct.getStatus();

		if (status != OrderProductStatus.PENDING_APPROVE) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
		}

		ProductEntity product = orderProduct.getProduct();
		InventoryEntity inventory = inventoryRepository.findByProductIdOrThrow(product.getId());
		inventory.addStock(orderProduct.getQuantity());
		orderProduct.cancel();
	}

	@Override
	public Page<SellerOrderResponse.Search> searchOrderProducts(Pageable pageable, OrderProductStatus status,
		UUID orderProductId, UUID sellerId) {
		return orderProductRepository.search(pageable, orderProductId, status, sellerId);
	}

	@Override
	public void confirmPaidOrderProduct(UUID orderProductId, UUID sellerId) {
		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);
		Preconditions.validate(orderProduct.getProduct().getSeller().getId().toString().equals(sellerId.toString()),
			ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		OrderProductStatus status = orderProduct.getStatus();

		if (status != OrderProductStatus.PENDING_APPROVE) {
			throw new CustomException(ErrorCode.INVALID_ORDER_PRODUCT_STATUS);
		}
		orderProduct.confirmPaidOrderProduct();
	}

}
