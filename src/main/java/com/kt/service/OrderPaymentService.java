package com.kt.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.PayRepository;
import com.kt.repository.product.ProductRepository;

import com.kt.repository.user.UserRepository;
import com.kt.service.payment.PaymentSettlementService;

import com.kt.domain.dto.request.OrderRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderPaymentService {

	private final OrderService orderService;
	private final InventoryService inventoryService;
	private final PaymentSettlementService paymentSettlementService;
	private final PayRepository payRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional
	public void orderPay(UUID buyerId, OrderRequest.Create request) {

		UserEntity buyer = userRepository.findByIdOrThrow(buyerId);
		BigDecimal totalAmount = calculateTotalAmount(request);
		validateBalance(buyer, totalAmount);

		inventoryService.reduceStock(request.items());

		OrderEntity order = orderService.createOrder(buyerId, request);

		order.getOrderProducts().forEach(
			orderProduct -> {
				paymentSettlementService.settleOrderProduct(
					buyer,
					orderProduct
				);
			});

	}

	private BigDecimal calculateTotalAmount(OrderRequest.Create request) {

		BigDecimal total = BigDecimal.ZERO;

		for (OrderRequest.Item item : request.items()) {
			ProductEntity product = productRepository.findByIdOrThrow(
				item.productId()
			);

			BigDecimal price = BigDecimal.valueOf(product.getPrice());

			BigDecimal itemTotal =
				price.multiply(BigDecimal.valueOf(item.quantity()));

			total = total.add(itemTotal);
		}

		return total;
	}

	private void validateBalance(UserEntity buyer, BigDecimal amount) {
		PayEntity pay = payRepository.findByUserOrThrow(buyer);

		if (pay.getBalance().compareTo(amount) < 0)
			throw new CustomException(ErrorCode.PAY_BALANCE_NOT_ENOUGH);
	}

}
