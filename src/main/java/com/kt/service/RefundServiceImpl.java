package com.kt.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.PaymentStatus;
import com.kt.constant.RefundStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.RefundHistoryEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.PayRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.refund.RefundHistoryRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

	private final UserRepository userRepository;
	private final OrderProductRepository orderProductRepository;
	private final PaymentRepository paymentRepository;
	private final RefundHistoryRepository refundHistoryRepository;
	private final PayRepository payRepository;

	@Override
	@Transactional
	public void requestRefund(UUID userId, UUID orderProductId, String reason) {

		UserEntity member = userRepository.findByIdOrThrow(userId);

		OrderProductEntity orderProduct =
			orderProductRepository.findByIdOrThrow(orderProductId);

		OrderEntity order = orderProduct.getOrder();
		if (!order.isOwnedBy(member)) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_NOT_ALLOWED);
		}

		if (orderProduct.getStatus() != OrderProductStatus.SHIPPING_COMPLETED) {
			throw new CustomException(ErrorCode.REFUND_NOT_ALLOWED);
		}

		PaymentEntity payment =
			paymentRepository.findByOrderProduct(orderProduct)
				.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (payment.getPaymentStatus() != PaymentStatus.PAID) {
			if (payment.getPaymentStatus() == PaymentStatus.REFUND_COMPLETED) {
				throw new CustomException(ErrorCode.ALREADY_REFUNDED);
			}
			throw new CustomException(ErrorCode.REFUND_NOT_ALLOWED);
		}

		boolean alreadyRequested =
			refundHistoryRepository.existsByOrderProductAndStatus(
				orderProduct,
				RefundStatus.REQUESTED
			);

		if (alreadyRequested) {
			throw new CustomException(ErrorCode.REFUND_ALREADY_REQUESTED);
		}

		long refundAmount =
			payment.getTotalProductPrice() + payment.getDeliveryPrice();

		RefundHistoryEntity refundHistory =
			RefundHistoryEntity.request(payment, orderProduct, refundAmount, reason);

		refundHistoryRepository.save(refundHistory);
	}


	@Override
	@Transactional
	public void approveRefund(UUID sellerId, UUID refundId) {
		RefundHistoryEntity refundHistory =
			refundHistoryRepository.findById(refundId)
				.orElseThrow(() -> new CustomException(ErrorCode.REFUND_NOT_ALLOWED));

		if (refundHistory.getStatus() != RefundStatus.REQUESTED) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}

		OrderProductEntity orderProduct = refundHistory.getOrderProduct();

		UUID productSellerId =
			orderProduct.getProduct().getSeller().getId();

		if (!productSellerId.equals(sellerId)) {
			throw new CustomException(ErrorCode.AUTH_PERMISSION_DENIED);
		}

		PaymentEntity payment = refundHistory.getPayment();

		if (payment.getPaymentStatus() == PaymentStatus.REFUND_COMPLETED) {
			throw new CustomException(ErrorCode.ALREADY_REFUNDED);
		}

		UserEntity user = orderProduct.getOrder().getOrderBy();
		PayEntity pay = payRepository.findByUser(user)
			.orElseThrow(() -> new CustomException(ErrorCode.PAY_NOT_FOUND));

		long amount = payment.getRefundAmount();

		pay.refund(amount);
		payment.refund();
		orderProduct.completeRefund();
		refundHistory.complete(sellerId);
	}

	@Override
	@Transactional
	public void rejectRefund(UUID sellerId, UUID refundId, String reason) {

		RefundHistoryEntity refundHistory =
			refundHistoryRepository.findById(refundId)
				.orElseThrow(() -> new CustomException(ErrorCode.REFUND_NOT_ALLOWED));

		if (refundHistory.getStatus() != RefundStatus.REQUESTED) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}

		OrderProductEntity orderProduct = refundHistory.getOrderProduct();

		UUID productSellerId =
			orderProduct.getProduct().getSeller().getId();

		if (!productSellerId.equals(sellerId)) {
			throw new CustomException(ErrorCode.AUTH_PERMISSION_DENIED);
		}

		refundHistory.reject(sellerId, reason);
	}
}