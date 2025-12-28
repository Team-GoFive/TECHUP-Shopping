package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.OrderEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.PaymentStatus;
import com.kt.constant.RefundStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.RefundHistoryEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.PayRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.refund.RefundHistoryRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class RefundServiceTest {

	@Autowired
	RefundService refundService;
	@Autowired
	OrderService orderService;
	@Autowired
	RefundHistoryRepository refundHistoryRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	PayRepository payRepository;

	UserEntity testUser;
	SellerEntity testSeller;
	OrderProductEntity testOrderProduct;


	OrderProductEntity createOrderProduct(
		OrderEntity order,
		CategoryEntity category,
		SellerEntity seller,
		long quantity
	) {
		ProductEntity product =
			productRepository.save(
				ProductEntityCreator.createProduct(category, seller)
			);

		OrderProductEntity orderProduct =
			OrderProductEntity.create(
				quantity,
				product.getPrice(),
				OrderProductStatus.SHIPPING_COMPLETED,
				order,
				product
			);

		order.addOrderProduct(orderProduct);
		return orderProductRepository.save(orderProduct);
	}

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		OrderEntity order = OrderEntityCreator.createOrderEntity(testUser);
		orderRepository.save(order);

		testOrderProduct =
			createOrderProduct(order, category, testSeller, 1L);

		PaymentEntity payment =
			PaymentEntity.create(
				10_000L,
				3_000L,
				testOrderProduct
			);
		paymentRepository.save(payment);
	}

	@Test
	void 환불_요청__성공() {
		// when
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"단순변심"
		);

		// then
		List<RefundHistoryEntity> histories =
			refundHistoryRepository.findAll();

		assertThat(histories).hasSize(1);
		assertThat(histories.get(0).getStatus())
			.isEqualTo(RefundStatus.REQUESTED);
	}

	@Test
	void 환불_요청__실패__중복요청() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		// when & then
		assertThatThrownBy(() ->
			refundService.requestRefund(
				testUser.getId(),
				testOrderProduct.getId(),
				"사유"
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.REFUND_ALREADY_REQUESTED.name());
	}

	@Test
	void 환불_요청__실패__배송완료_상태_아님() {
		// given
		testOrderProduct.updateStatus(OrderProductStatus.SHIPPING);

		// when & then
		assertThatThrownBy(() ->
			refundService.requestRefund(
				testUser.getId(),
				testOrderProduct.getId(),
				"사유"
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.REFUND_NOT_ALLOWED.name());
	}

	@Test
	void 환불_요청__실패__타인의_주문() {
		// given
		UserEntity otherUser = UserEntityCreator.create();
		userRepository.save(otherUser);

		// when & then
		assertThatThrownBy(() ->
			refundService.requestRefund(
				otherUser.getId(),
				testOrderProduct.getId(),
				"사유"
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ACCESS_NOT_ALLOWED.name());
	}


	@Test
	void 환불_승인__성공() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		// when
		refundService.approveRefund(
			testSeller.getId(),
			history.getId()
		);

		// then
		assertThat(history.getStatus())
			.isEqualTo(RefundStatus.COMPLETED);

		assertThat(testOrderProduct.getStatus())
			.isEqualTo(OrderProductStatus.REFUND_COMPLETED);
	}

	@Test
	void 환불_승인_성공__Pay_잔액_복구_성공() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"단순변심"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		PayEntity payBefore =
			payRepository.findByUser(testUser).orElseThrow();

		BigDecimal beforeBalance = payBefore.getBalance();

		// when
		refundService.approveRefund(
			testSeller.getId(),
			history.getId()
		);

		// then
		PayEntity payAfter =
			payRepository.findByUser(testUser).orElseThrow();

		BigDecimal refundAmount =
			BigDecimal.valueOf(history.getPayment().getRefundAmount());

		assertThat(payAfter.getBalance())
			.isEqualByComparingTo(beforeBalance.add(refundAmount));
	}

	@Test
	void 환불_승인_성공__결제상태_COMPLETE_REFUND() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		// when
		refundService.approveRefund(
			testSeller.getId(),
			history.getId()
		);

		// then
		PaymentEntity payment =
			paymentRepository.findByOrderProduct(testOrderProduct)
				.orElseThrow();

		assertThat(payment.getPaymentStatus())
			.isEqualTo(PaymentStatus.REFUND_COMPLETED);
	}


	@Test
	void 환불_승인__실패__이미_완료됨() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		refundService.approveRefund(
			testSeller.getId(),
			history.getId()
		);

		// when & then
		assertThatThrownBy(() ->
			refundService.approveRefund(
				testSeller.getId(),
				history.getId()
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_FORCE_STATUS_TRANSITION.name());
	}

	@Test
	void 환불_승인__실패__다른_셀러의_상품() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		SellerEntity otherSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(otherSeller);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		// when & then
		assertThatThrownBy(() ->
			refundService.approveRefund(
				otherSeller.getId(),
				history.getId()
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.AUTH_PERMISSION_DENIED.name());
	}


	@Test
	void 환불_거부__성공() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		// when
		refundService.rejectRefund(
			testSeller.getId(),
			history.getId(),
			"거부 사유"
		);

		// then
		assertThat(history.getStatus())
			.isEqualTo(RefundStatus.REJECTED);

		assertThat(testOrderProduct.getStatus())
			.isEqualTo(OrderProductStatus.SHIPPING_COMPLETED);
	}

	@Test
	void 환불_거부_후_재처리_불가() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		refundService.rejectRefund(
			testSeller.getId(),
			history.getId(),
			"거부"
		);

		// when & then
		assertThatThrownBy(() ->
			refundService.approveRefund(
				testSeller.getId(),
				history.getId()
			)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_FORCE_STATUS_TRANSITION.name());
	}

	@Test
	void 환불_거부__Pay_잔액_변경_없음() {
		// given
		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"사유"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		PayEntity payBefore =
			payRepository.findByUser(testUser).orElseThrow();

		BigDecimal beforeBalance = payBefore.getBalance();

		// when
		refundService.rejectRefund(
			testSeller.getId(),
			history.getId(),
			"거부"
		);

		// then
		PayEntity payAfter =
			payRepository.findByUser(testUser).orElseThrow();

		assertThat(payAfter.getBalance())
			.isEqualByComparingTo(beforeBalance);
	}


	@Test
	void 배송전_주문취소__Pay_잔액_복구_성공() {
		// given
		OrderProductEntity cancelableOrderProduct =
			OrderProductEntity.create(
				1L,
				10_000L,
				OrderProductStatus.PENDING_APPROVE,
				testOrderProduct.getOrder(),
				testOrderProduct.getProduct()
			);
		orderProductRepository.save(cancelableOrderProduct);

		PaymentEntity payment =
			PaymentEntity.create(
				10_000L,
				3_000L,
				cancelableOrderProduct
			);
		paymentRepository.save(payment);

		PayEntity payBefore =
			payRepository.findByUser(testUser).orElseThrow();

		BigDecimal beforeBalance = payBefore.getBalance();

		// when
		orderService.cancelOrderProduct(
			testUser.getId(),
			cancelableOrderProduct.getId()
		);

		// then
		PayEntity payAfter =
			payRepository.findByUser(testUser).orElseThrow();

		BigDecimal refundAmount =
			BigDecimal.valueOf(payment.getRefundAmount());

		assertThat(payAfter.getBalance())
			.isEqualByComparingTo(beforeBalance.add(refundAmount));
	}

	@Test
	void 환불_승인_성공__재고_복구_성공() {
		// given
		long quantity = testOrderProduct.getQuantity();
		ProductEntity product = testOrderProduct.getProduct();

		long beforeStock = product.getStock();

		refundService.requestRefund(
			testUser.getId(),
			testOrderProduct.getId(),
			"단순변심"
		);

		RefundHistoryEntity history =
			refundHistoryRepository.findAll().get(0);

		// when
		refundService.approveRefund(
			testSeller.getId(),
			history.getId()
		);

		// then
		ProductEntity afterProduct =
			productRepository.findById(product.getId())
				.orElseThrow();

		assertThat(afterProduct.getStock())
			.isEqualTo(beforeStock + quantity);
	}


}