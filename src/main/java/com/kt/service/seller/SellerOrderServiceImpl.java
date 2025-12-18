package com.kt.service.seller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.SellerOrderResponse;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.exception.CustomException;
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

	@Override
	public void cancelOrderProduct(UUID orderProductId, UUID sellerId) {
		OrderProductEntity orderProduct = getOrderProductWithOwnerCheck(orderProductId, sellerId);

		OrderProductStatus status = orderProduct.getStatus();

		if (status != OrderProductStatus.SHIPPING_READY && status != OrderProductStatus.PAID) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
		}

		ProductEntity product = orderProduct.getProduct();
		product.addStock(orderProduct.getQuantity());
		orderProduct.cancel();
	}

	@Override
	public Page<SellerOrderResponse.Search> searchOrderProducts(Pageable pageable, OrderProductStatus status,
		UUID orderProductId, UUID sellerId) {
		return orderProductRepository.search(pageable, orderProductId, status, sellerId);
	}

	@Override
	public void confirmPaidOrderProduct(UUID orderProductId, UUID sellerId) {
		OrderProductEntity orderProduct = getOrderProductWithOwnerCheck(orderProductId, sellerId);
		OrderProductStatus status = orderProduct.getStatus();

		if (status != OrderProductStatus.PAID) {
			throw new CustomException(ErrorCode.INVALID_ORDER_PRODUCT_STATUS);
		}
		orderProduct.confirmPaidOrderProduct();
	}

	private OrderProductEntity getOrderProductWithOwnerCheck(UUID orderProductId, UUID sellerId) {
		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);
		SellerEntity seller = sellerRepository.findByIdOrThrow(sellerId);
		Preconditions.validate(orderProduct.getProduct().getSeller().getId().equals(seller.getId()),
			ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		return orderProduct;
	}

}
