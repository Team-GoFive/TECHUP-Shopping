package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.exception.CustomException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderStatus;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceTest {

	@Autowired
	private OrderService orderService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private CategoryEntity defaultCategory;

	@BeforeEach
	void setup() {
		reviewRepository.deleteAll();
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();
		categoryRepository.deleteAll();

		CategoryEntity category = CategoryEntity.create("테스트 기본 카테고리", null);
		defaultCategory = categoryRepository.save(category);
	}

	UserEntity createUser(String email) {
		return userRepository.save(
			UserEntity.create(
				"테스트유저",
				email,
				"111",
				UserRole.MEMBER,
				Gender.MALE,
				LocalDate.of(2000, 1, 1),
				"010-1234-5678"
			)
		);
	}

	ProductEntity createProduct(String name, long stock, long price) {
		return productRepository.save(
			ProductEntity.create(name, stock, price, defaultCategory)
		);
	}

	OrderEntity createOrder(UserEntity user, OrderStatus status) {
		return orderRepository.save(
			OrderEntity.create(
				ReceiverVO.create(
					"수령인",
					"010-0000-0000",
					"서울시",
					"강남구",
					"테헤란로",
					"101호"
				),
				user,
				status
			)
		);
	}

	OrderProductEntity createOrderWithProducts(OrderEntity order, ProductEntity product, long quantity) {

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
		UserEntity user = createUser("test@example.com");
		ProductEntity p1 = createProduct("상품1", 10L, 10000L);
		ProductEntity p2 = createProduct("상품2", 5L, 20000L);

		OrderRequest.Item item1 = new OrderRequest.Item(p1.getId(), 2L);
		OrderRequest.Item item2 = new OrderRequest.Item(p2.getId(), 1L);
		List<OrderRequest.Item> items = List.of(item1, item2);

		// when
		orderService.createOrder(user.getEmail(), items);

		// then
		List<OrderEntity> orders = orderRepository.findAll();
		assertThat(orders).isNotEmpty();
		OrderEntity savedOrder = orders.get(orders.size() - 1);

		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAll()
				.stream()
				.filter(orderProductEntity -> orderProductEntity.getOrder().getId().equals(savedOrder.getId()))
				.toList();

		assertThat(orderProducts).hasSize(2);

		ProductEntity finalProduct1 = productRepository.findById(p1.getId()).get();
		ProductEntity finalProduct2 = productRepository.findById(p2.getId()).get();

		assertThat(finalProduct1.getStock()).isEqualTo(10000L - 2L);
		assertThat(finalProduct2.getStock()).isEqualTo(20000L - 1L);

		assertThat(orderProducts.get(0).getQuantity()).isGreaterThan(0);
		assertThat(orderProducts.get(1).getQuantity()).isGreaterThan(0);
	}

	@Test
	void 주문_생성_실패__상품없음() {
		// given
		UserEntity user = createUser("test@example.com");

		UUID invalidId = UUID.fromString("11111111-2222-3333-4444-555555555555");
		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(invalidId, 1L));

		// when, then
		assertThatThrownBy(() -> orderService.createOrder(user.getEmail(), items))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("PRODUCT_NOT_FOUND");
	}

	@Test
	void 주문_생성_실패__재고부족() {

		// given
		UserEntity user = UserEntity.create(
			"김도현",
			"test@example.com",
			"111",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"0101010"
		);

		ProductEntity product = productRepository.save(
			ProductEntity.create("상품1", 3L, 1L, defaultCategory)
		);

		UserEntity savedUser = userRepository.save(user);
		ProductEntity savedProduct = productRepository.save(product);
		// given

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(savedProduct.getId(), 2L)
		);

		// then
		assertThatThrownBy(() -> orderService.createOrder(savedUser.getEmail(), items))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("STOCK_NOT_ENOUGH");
	}

	@Test
	void 주문상품_조회() {
		// given
		UserEntity user = createUser("test@example.com");
		ProductEntity product = createProduct("테스트상품", 10L, 10000L);

		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		createOrderWithProducts(order, product, 2L);
		OrderEntity savedOrder = orderRepository.save(order);

		// when
		OrderResponse.OrderProducts foundOrderProduct = orderService.getOrderProducts(savedOrder.getId());

		// then
		assertThat(foundOrderProduct).isNotNull();
		assertThat(foundOrderProduct.orderId()).isEqualTo(savedOrder.getId());
		assertThat(foundOrderProduct.orderProducts()).isNotEmpty();
		assertThat(foundOrderProduct.orderProducts().size()).isEqualTo(1);
	}

	@Test
	void 주문_취소_성공() {
		// given
		UserEntity user = createUser("cancel@example.com");
		ProductEntity product = createProduct("상품", 10000L, 5000L);
		productRepository.save(product);
		OrderEntity order = createOrder(user, OrderStatus.WAITING_PAYMENT);

		OrderProductEntity orderProduct = createOrderWithProducts(order, product, 3L);
		long beforeStock = product.getStock();

		UUID orderId = order.getId();

		// when
		orderService.cancelOrder(orderId);

		// then
		OrderProductEntity afterOp = orderProductRepository.findById(orderProduct.getId()).get();
		assertThat(afterOp.getStatus()).isEqualTo(OrderProductStatus.CANCELED);

		ProductEntity finalP = productRepository.findById(product.getId()).get();
		assertThat(finalP.getStock()).isEqualTo(beforeStock + 3L);
	}

	@Test
	void 주문_취소_실패__이미_구매확정() {
		// given
		UserEntity user = createUser("fail@example.com");
		OrderEntity order = createOrder(user, OrderStatus.PURCHASE_CONFIRMED);

		// when & then
		assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("ORDER_ALREADY_CONFIRMED");
	}

	@Test
	void 주문_수정_성공__배송정보_변경() {
		// given
		UserEntity user = createUser("update@example.com");
		ProductEntity product = createProduct("상품", 10000L, 5000L);
		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		createOrderWithProducts(order, product, 2L);

		OrderRequest.Update updateRequest = new OrderRequest.Update(
			"박수정",
			"01099998888",
			"서울특별시",
			"강동구",
			"김김대로",
			"2층"
		);

		// when
		orderService.updateOrder(order.getId(), updateRequest);

		// then
		OrderEntity updated = orderRepository.findById(order.getId()).get();
		assertThat(updated.getReceiverVO().getName()).isEqualTo("박수정");
	}

	@Test
	void 주문_수정_실패__이미_처리됨() {
		// given
		UserEntity user = createUser("fail2@example.com");
		OrderEntity order = createOrder(user, OrderStatus.PURCHASE_CONFIRMED);

		OrderRequest.Update req = new OrderRequest.Update(
			"이름",
			"010",
			"도시",
			"동네",
			"도로",
			"상세"
		);

		// then
		assertThatThrownBy(() -> orderService.updateOrder(order.getId(), req))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("ORDER_ALREADY_CONFIRMED");
	}

	@Test
	void 주문_리스트_조회_성공() {
		// given
		UserEntity admin = userRepository.save(
			UserEntity.create(
				"관리자",
				"admin@example.com",
				"111",
				UserRole.ADMIN,
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				"010-0000-0000"
			)
		);

		UserEntity user = createUser("user1@example.com");
		ProductEntity p = createProduct("상품1", 100L, 10000L);

		OrderEntity order1 = createOrder(user, OrderStatus.CREATED);
		OrderEntity order2 = createOrder(user, OrderStatus.WAITING_PAYMENT);

		createOrderWithProducts(order1, p, 1L);
		createOrderWithProducts(order2, p, 2L);

		Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

		// when
		Page<AdminOrderResponse.Search> result = orderService.searchOrder(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isEqualTo(2);
		assertThat(result.getContent()
			.stream()
			.map(AdminOrderResponse.Search::orderId)
			.toList()
		).contains(order1.getId(), order2.getId());
	}

	@Test
	void 주문_상세_조회_성공() {
		// given
		UserEntity admin = userRepository.save(
			UserEntity.create(
				"관리자",
				"admin2@example.com",
				"111",
				UserRole.ADMIN,
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				"010-0000-0000"
			)
		);

		UserEntity user = createUser("detailUser@example.com");
		ProductEntity p = createProduct("상세상품", 50L, 20000L);

		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		OrderProductEntity op = createOrderWithProducts(order, p, 3L);

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
		UserEntity admin = userRepository.save(
			UserEntity.create(
				"관리자",
				"admin3@example.com",
				"111",
				UserRole.ADMIN,
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				"010-0000-0000"
			)
		);

		UserEntity user = createUser("statusUser@example.com");
		ProductEntity p = createProduct("상태상품", 100L, 10000L);

		OrderEntity order = createOrder(user, OrderStatus.CREATED);
		createOrderWithProducts(order, p, 1L);

		// 변경할 상태
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
		UserEntity admin = userRepository.save(
			UserEntity.create(
				"관리자",
				"admin4@example.com",
				"111",
				UserRole.ADMIN,
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				"010-0000-0000"
			)
		);

		UserEntity user = createUser("deliveryUser@example.com");
		ProductEntity product = createProduct("배송상품", 100L, 10000L);

		OrderEntity order = createOrder(user, OrderStatus.SHIPPING);
		createOrderWithProducts(order, product, 1L);

		OrderStatus newStatus = OrderStatus.CANCELED;

		// when & then
		assertThatThrownBy(() ->
			orderService.updateOrderStatus(order.getId(), newStatus)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}



}
