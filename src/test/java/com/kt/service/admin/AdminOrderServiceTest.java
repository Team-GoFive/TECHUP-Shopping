package com.kt.service.admin;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
import com.kt.constant.OrderStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
	ReviewRepository reviewRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	AddressRepository addressRepository;

	CategoryEntity category;

	@BeforeEach
	void setup() {
		reviewRepository.deleteAll();
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();
		categoryRepository.deleteAll();
		addressRepository.deleteAll();

		category = categoryRepository.save(CategoryEntityCreator.createCategory());
	}

	OrderEntity createOrder(UserEntity user, OrderStatus status) {
		return orderRepository.save(
			OrderEntity.create(
				ReceiverCreator.createReceiver(),
				user,
				status
			)
		);
	}

	OrderProductEntity createOrderWithProducts(OrderEntity order, long quantity) {

		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));
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
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));
		OrderEntity order = createOrder(user, OrderStatus.WAITING_PAYMENT);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 3L);
		long beforeStock = orderProduct.getProduct().getStock();

		// when
		adminOrderService.cancelOrder(user.getId(), order.getId());

		// then
		long afterStock = productRepository.findById(orderProduct.getProduct().getId()).get().getStock();
		assertThat(afterStock).isEqualTo(beforeStock + 3L);
	}

	@Test
	void 주문_취소_실패__이미_구매확정() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = createOrder(user, OrderStatus.PURCHASE_CONFIRMED);

		// when & then
		assertThatThrownBy(() -> adminOrderService.cancelOrder(user.getId(), order.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("ORDER_ALREADY_CONFIRMED");
	}

	@Test
	void 주문_리스트_조회_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));

		OrderEntity order1 = createOrder(user, OrderStatus.CREATED);
		OrderEntity order2 = createOrder(user, OrderStatus.WAITING_PAYMENT);

		product.decreaseStock(1L);
		productRepository.save(product);
		orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order1,
				product
			)
		);

		product.decreaseStock(2L);
		productRepository.save(product);
		orderProductRepository.save(
			OrderProductEntity.create(
				2L,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order2,
				product
			)
		);

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
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));

		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		product.decreaseStock(3L);
		productRepository.save(product);
		orderProductRepository.save(
			OrderProductEntity.create(
				3L,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			)
		);

		// when
		AdminOrderResponse.Detail detail = adminOrderService.getOrderDetail(order.getId());

		// then
		assertThat(detail).isNotNull();
		assertThat(detail.orderId()).isEqualTo(order.getId());
		assertThat(detail.ordererName()).isEqualTo(user.getName());
		assertThat(detail.products()).hasSize(1);
		assertThat(detail.products().get(0).quantity()).isEqualTo(3L);
	}

	@Test
	void 주문_상태_변경_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));

		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		product.decreaseStock(1L);
		productRepository.save(product);
		orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			)
		);
		OrderStatus newStatus = OrderStatus.SHIPPING;

		// when
		adminOrderService.updateOrderStatus(order.getId(), newStatus);

		// then
		OrderEntity updated = orderRepository.findById(order.getId()).get();
		assertThat(updated.getStatus()).isEqualTo(newStatus);
	}

	@Test
	void 주문_상태_변경_실패__현재배송중() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));

		OrderEntity order = createOrder(user, OrderStatus.SHIPPING);
		product.decreaseStock(1L);
		productRepository.save(product);
		orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			)
		);

		OrderStatus newStatus = OrderStatus.CANCELED;

		// when & then
		assertThatThrownBy(() ->
			adminOrderService.updateOrderStatus(order.getId(), newStatus)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}

}