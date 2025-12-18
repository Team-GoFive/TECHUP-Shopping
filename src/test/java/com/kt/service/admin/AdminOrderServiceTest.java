package com.kt.service.admin;

import com.kt.common.AdminCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.AdminEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.admin.AdminRepository;
import com.kt.repository.seller.SellerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AdminOrderServiceTest {

	@Autowired
	AdminOrderService adminOrderService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	AdminRepository adminRepository;

	CategoryEntity category;
	SellerEntity testSeller;

	@BeforeEach
	void setup() {
		category = categoryRepository.save(CategoryEntityCreator.createCategory());
		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);
	}

	OrderEntity createOrder(UserEntity user) {
		return orderRepository.save(
			OrderEntity.create(
				ReceiverCreator.createReceiver(),
				user
			)
		);
	}

	OrderProductEntity createOrderWithProducts(OrderEntity order, long quantity) {

		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category, testSeller));
		product.decreaseStock(quantity);
		productRepository.save(product);

		return orderProductRepository.save(
			OrderProductEntity.create(
				quantity,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			)
		);
	}

	@Test
	void 주문_취소_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct1 = createOrderWithProducts(order, 3L);
		OrderProductEntity orderProduct2 = createOrderWithProducts(order, 2L);

		orderProduct1.updateStatus(OrderProductStatus.SHIPPING_READY);
		orderProduct2.updateStatus(OrderProductStatus.SHIPPING_READY);

		long beforeStock1 = orderProduct1.getProduct().getStock();
		long beforeStock2 = orderProduct2.getProduct().getStock();

		// when
		adminOrderService.cancelOrderProduct(user.getId(), orderProduct1.getId());
		adminOrderService.cancelOrderProduct(user.getId(), orderProduct2.getId());

		// then
		OrderProductEntity canceled1 =
			orderProductRepository.findById(orderProduct1.getId()).orElseThrow();
		OrderProductEntity canceled2 =
			orderProductRepository.findById(orderProduct2.getId()).orElseThrow();

		assertThat(canceled1.getStatus()).isEqualTo(OrderProductStatus.CANCELED);
		assertThat(canceled2.getStatus()).isEqualTo(OrderProductStatus.CANCELED);

		long afterStock1 = productRepository
			.findById(orderProduct1.getProduct().getId())
			.orElseThrow().getStock();
		long afterStock2 = productRepository
			.findById(orderProduct2.getProduct().getId())
			.orElseThrow()
			.getStock();

		assertThat(afterStock1).isEqualTo(beforeStock1 + 3L);
		assertThat(afterStock2).isEqualTo(beforeStock2 + 2L);
	}

	@Test
	void 주문_취소_실패__이미_구매확정() {
		// given
		UserEntity testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), testUser)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.PURCHASE_CONFIRMED);

		// when & then
		assertThatThrownBy(() -> adminOrderService.cancelOrderProduct(testUser.getId(), orderProduct.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_CONFIRMED.name());
	}

	@Test
	void 주문_리스트_조회_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order1 = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);
		OrderEntity order2 = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		createOrderWithProducts(order1, 1L);
		createOrderWithProducts(order2, 1L);

		Pageable pageable = Pageable.ofSize(10);

		// when
		Page<AdminOrderResponse.Search> result = adminOrderService.searchOrder(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()
			.stream()
			.map(AdminOrderResponse.Search::orderId)
		).contains(order1.getId(), order2.getId());
	}

	@Test
	void 주문_상세_조회_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());

		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 3L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		// when
		AdminOrderResponse.Detail detail = adminOrderService.getOrderDetail(order.getId());

		// then
		assertThat(detail).isNotNull();
		assertThat(detail.orderId()).isEqualTo(order.getId());
		assertThat(detail.ordererId()).isEqualTo(user.getId());
		assertThat(detail.products()).hasSize(1);
		assertThat(detail.products().get(0).quantity()).isEqualTo(3L);
	}

	@Test
	void 주문상품_상태_강제변경_성공() {
		// given
		AdminEntity adminEntity = AdminCreator.create();
		adminRepository.save(adminEntity);

		OrderEntity order = createOrder(adminEntity);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		// when
		adminOrderService.forceChangeStatus(
			orderProduct.getId(),
			OrderProductStatus.SHIPPING_READY
		);

		// then
		OrderProductEntity updated =
			orderProductRepository.findById(orderProduct.getId()).orElseThrow();

		assertThat(updated.getStatus()).isEqualTo(OrderProductStatus.SHIPPING_READY);
	}

	@Test
	void 주문상품_상태_강제변경_실패__잘못된_상태_전이() {
		// given
		AdminEntity adminEntity = AdminCreator.create();
		adminRepository.save(adminEntity);

		OrderEntity order = createOrder(adminEntity);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 1L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		// when & then
		assertThatThrownBy(() ->
			adminOrderService.forceChangeStatus(
				orderProduct.getId(),
				OrderProductStatus.PURCHASE_CONFIRMED
			)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_FORCE_STATUS_TRANSITION.name());
	}


}