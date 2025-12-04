package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.AddressCreator;
import com.kt.common.CategoryEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.AddressEntity;
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
class OrderServiceTest {

	@Autowired
	OrderService orderService;
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
	void 주문_생성_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		AddressEntity address = addressRepository.save(AddressCreator.createAddress(user));

		ProductEntity product1 = productRepository.save(ProductEntityCreator.createProduct(category));
		ProductEntity product2 = productRepository.save(ProductEntityCreator.createProduct(category));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(product1.getId(), 2L),
			new OrderRequest.Item(product2.getId(), 3L)
		);

		// when
		orderService.createOrder(user.getEmail(), items, address.getId());

		// then
		OrderEntity savedOrder = orderRepository.findAll().get(0);

		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(savedOrder.getId());
		assertThat(orderProducts).hasSize(2);
	}

	@Test
	void 주문_생성_실패__상품없음() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		AddressEntity address = addressRepository.save(AddressCreator.createAddress(user));

		UUID invalidId = UUID.fromString("11111111-2222-3333-4444-555555555555");
		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(invalidId, 1L));

		// when, then
		assertThatThrownBy(() -> orderService.createOrder(user.getEmail(), items, address.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("PRODUCT_NOT_FOUND");
	}

	@Test
	void 주문_생성_실패__재고부족() {

		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		AddressEntity address = addressRepository.save(AddressCreator.createAddress(user));
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(product.getId(), 99999999L)
		);

		// then
		assertThatThrownBy(() -> orderService.createOrder(user.getEmail(), items, address.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("STOCK_NOT_ENOUGH");
	}

	@Test
	void 주문상품_조회() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = createOrder(user, OrderStatus.CREATED);

		createOrderWithProducts(order, 2L);

		// when
		OrderResponse.OrderProducts foundOrderProduct = orderService.getOrderProducts(order.getId());

		// then
		assertThat(foundOrderProduct).isNotNull();
		assertThat(foundOrderProduct.orderId()).isEqualTo(order.getId());
		assertThat(foundOrderProduct.orderProducts()).isNotEmpty();
		assertThat(foundOrderProduct.orderProducts().size()).isEqualTo(1);
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
		orderService.cancelOrder(user.getId(), order.getId());

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
		assertThatThrownBy(() -> orderService.cancelOrder(user.getId(), order.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("ORDER_ALREADY_CONFIRMED");
	}

	@Test
	void 주문_수정_성공__배송정보_변경() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = createOrder(user, OrderStatus.WAITING_PAYMENT);

		createOrderWithProducts(order, 2L);

		OrderRequest.Update updateRequest = new OrderRequest.Update(
			"박수정",
			"01099998888",
			"서울특별시",
			"강동구",
			"김김대로",
			"2층"
		);

		// when
		orderService.updateOrder(user.getId(), order.getId(), updateRequest);

		// then
		assertThat(orderRepository.findById(order.getId()).get()
			.getReceiverVO().getName()).isEqualTo("박수정");
	}

	@Test
	void 주문_수정_실패__이미_처리됨() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = createOrder(user, OrderStatus.PURCHASE_CONFIRMED);

		OrderRequest.Update request = new OrderRequest.Update(
			"이름",
			"010",
			"도시",
			"동네",
			"도로",
			"상세"
		);

		// then
		assertThatThrownBy(() -> orderService.updateOrder(user.getId(), order.getId(), request))
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
		Page<AdminOrderResponse.Search> result = orderService.searchOrder(pageable);

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
		AdminOrderResponse.Detail detail = orderService.getOrderDetail(order.getId());

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
		orderService.updateOrderStatus(order.getId(), newStatus);

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
			orderService.updateOrderStatus(order.getId(), newStatus)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}

}
