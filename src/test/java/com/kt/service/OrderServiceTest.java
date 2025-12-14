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
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
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
		category = categoryRepository.save(CategoryEntityCreator.createCategory());
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

	// TODO: 위치 변경 필요
	@Test
	void 주문상품_조회() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 3L);
		orderProduct.updateStatus(OrderProductStatus.PAID);

		// when
		OrderResponse.OrderProducts foundOrderProduct = orderService.getOrderProducts(order.getId());

		// then
		assertThat(foundOrderProduct).isNotNull();
		assertThat(foundOrderProduct.orderId()).isEqualTo(order.getId());
		assertThat(foundOrderProduct.orderProducts()).isNotEmpty();
		assertThat(foundOrderProduct.orderProducts().size()).isEqualTo(1);
	}

	@Test
	void 주문_취소_성공__모든_주문상품_PAID() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct1 = createOrderWithProducts(order, 3L);
		OrderProductEntity orderProduct2 = createOrderWithProducts(order, 2L);

		orderProduct1.updateStatus(OrderProductStatus.PAID);
		orderProduct2.updateStatus(OrderProductStatus.PAID);

		long beforeStock1 = orderProduct1.getProduct().getStock();
		long beforeStock2 = orderProduct2.getProduct().getStock();

		// when
		orderService.cancelOrderProduct(user.getId(), order.getId());

		// then
		OrderProductEntity canceled1 =
			orderProductRepository.findById(orderProduct1.getId()).orElseThrow();
		OrderProductEntity canceled2 =
			orderProductRepository.findById(orderProduct2.getId()).orElseThrow();

		assertThat(canceled1.getStatus()).isEqualTo(OrderProductStatus.CANCELED);
		assertThat(canceled2.getStatus()).isEqualTo(OrderProductStatus.CANCELED);

		long afterStock1 = productRepository
			.findById(orderProduct1.getProduct().getId())
			.orElseThrow()
			.getStock();
		long afterStock2 = productRepository
			.findById(orderProduct2.getProduct().getId())
			.orElseThrow()
			.getStock();

		assertThat(afterStock1).isEqualTo(beforeStock1 + 3L);
		assertThat(afterStock2).isEqualTo(beforeStock2 + 2L);
	}

	@Test
	void 주문_취소_실패__주문상품_배송중_상태() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.SHIPPING);

		// when & then
		assertThatThrownBy(() -> orderService.cancelOrderProduct(user.getId(), order.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}

	@Test
	void 주문_수정_성공__배송정보_변경() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.PAID);

		OrderRequest.Update updateRequest = new OrderRequest.Update(
			"박수정",
			"01099998888",
			"서울특별시",
			"강동구",
			"김김대로",
			"2층"
		);

		// when
		orderService.changeOrderAddress(user.getId(), order.getId(), updateRequest);

		// then
		assertThat(orderRepository.findById(order.getId()).get()
			.getReceiverVO().getName()).isEqualTo("박수정");
	}

	@Test
	void 주문_수정_실패__배송중() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.SHIPPING);

		OrderRequest.Update request = new OrderRequest.Update(
			"이름",
			"010",
			"도시",
			"동네",
			"도로",
			"상세"
		);

		// then
		assertThatThrownBy(() -> orderService.changeOrderAddress(user.getId(), order.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}

	// TODO: 조회 API용 DTO 매핑 테스트로 위치 변경
	@Test
	void 주문_리스트_조회_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());

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
		Page<AdminOrderResponse.Search> result = orderService.searchOrder(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()
			.stream()
			.map(AdminOrderResponse.Search::orderId)
		).contains(order1.getId(), order2.getId());
	}

	// TODO: 위치 변경 필요
	@Test
	void 주문_상세_조회_성공() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.createMember());

		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		createOrderWithProducts(order, 3L);

		// when
		AdminOrderResponse.Detail detail = orderService.getOrderDetail(order.getId());

		// then
		assertThat(detail).isNotNull();
		assertThat(detail.orderId()).isEqualTo(order.getId());
		assertThat(detail.ordererId()).isEqualTo(user.getId());
		assertThat(detail.products()).hasSize(1);
		assertThat(detail.products().get(0).quantity()).isEqualTo(3L);
	}

	/*예전 정책: 관리자가 직접 주문 상태를 PAID->SHIPPING으로 변경.
	* 현재는 배송기사가 추가되었기 떄문에 관리자가 주문 진행 상태를 마음대로 변경하지 않아도 된다.
	* 삭제함.
	* */

}
