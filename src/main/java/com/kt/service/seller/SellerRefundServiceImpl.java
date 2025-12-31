package com.kt.service.seller;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.PaymentStatus;
import com.kt.constant.RefundStatus;
import com.kt.constant.message.ErrorCode;
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
public class SellerRefundServiceImpl implements SellerRefundService {

	private final UserRepository userRepository;
	private final OrderProductRepository orderProductRepository;
	private final PaymentRepository paymentRepository;
	private final RefundHistoryRepository refundHistoryRepository;
	private final PayRepository payRepository;

	@Override
	@Transactional
	public void approveRefund(UUID sellerId, UUID refundId) {
		RefundHistoryEntity refundHistory =
			refundHistoryRepository.findByIdOrThrow(refundId);

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
		PayEntity pay =
			payRepository.findByUserOrThrow(user);

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
			refundHistoryRepository.findByIdOrThrow(refundId);

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
