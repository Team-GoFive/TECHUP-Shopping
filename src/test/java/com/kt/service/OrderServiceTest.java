package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.AddressCreator;
import com.kt.common.CategoryEntityCreator;
import com.kt.common.CourierEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.ShippingDetailEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.ShippingDetailRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

	@Autowired
	OrderService orderService;
	@Autowired
	InventoryService inventoryService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	InventoryRepository inventoryRepository;
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
	@Autowired
	ShippingDetailRepository shippingDetailRepository;
	@Autowired
	CourierRepository courierRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	PaymentRepository paymentRepository;

	CategoryEntity category;
	SellerEntity testSeller;
	UserEntity testUser;

	@BeforeEach
	void setup() {
		category = CategoryEntityCreator.createCategory();
		testSeller = SellerEntityCreator.createSeller();
		testUser = UserEntityCreator.create();

		userRepository.save(testUser);
		sellerRepository.save(testSeller);
		categoryRepository.save(category);
	}

	OrderProductEntity createOrderWithProducts(OrderEntity order, long quantity) {
		ProductEntity product = ProductEntityCreator.createProduct(category, testSeller);
		productRepository.save(product);
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 1000L);
		inventoryRepository.save(inventory);

		OrderProductEntity orderProduct = OrderProductEntity.create(
			quantity,
			product.getPrice(),
			OrderProductStatus.CREATED,
			order,
			product
		);

		order.addOrderProduct(orderProduct);

		return orderProductRepository.save(orderProduct);
	}

	@Test
	void 주문_생성_성공() {
		// given
		AddressEntity address = AddressCreator.createAddress(testUser);
		addressRepository.save(address);

		ProductEntity product1 = productRepository.save(ProductEntityCreator.createProduct(category, testSeller));
		ProductEntity product2 = productRepository.save(ProductEntityCreator.createProduct(category, testSeller));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(product1.getId(), 2L),
			new OrderRequest.Item(product2.getId(), 3L)
		);

		OrderRequest orderRequest = new OrderRequest(
			items,
			address.getId()
		);

		// when
		orderService.createOrder(
			testUser.getId(),
			orderRequest
		);

		// then
		OrderEntity savedOrder = orderRepository.findAll().get(0);

		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(savedOrder.getId());
		assertThat(orderProducts).hasSize(2);
	}

	@Test
	void 주문_생성_실패__상품없음() {
		AddressEntity address = AddressCreator.createAddress(testUser);
		addressRepository.save(address);

		UUID invalidId = UUID.fromString("11111111-2222-3333-4444-555555555555");
		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(invalidId, 1L));

		OrderRequest orderRequest = new OrderRequest(items, address.getId());

		// when, then
		assertThatThrownBy(() -> {
				orderService.checkStock(orderRequest.items());
				orderService.createOrder(
					testUser.getId(),
					orderRequest
				);
				inventoryService.reduceStock(orderRequest.items());
			}
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("PRODUCT_NOT_FOUND");
	}

	@Test
	void 주문_생성_실패__재고부족() {

		AddressEntity address = AddressCreator.createAddress(testUser);
		addressRepository.save(address);
		ProductEntity product = productRepository.save(ProductEntityCreator.createProduct(category, testSeller));
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
		inventoryRepository.save(inventory);

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(product.getId(), 99999999L)
		);

		OrderRequest orderRequest = new OrderRequest(items, address.getId());

		// when, then
		assertThatThrownBy(() -> {
				orderService.checkStock(orderRequest.items());
				orderService.createOrder(
					testUser.getId(),
					orderRequest
				);
				inventoryService.reduceStock(orderRequest.items());
			}
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("STOCK_NOT_ENOUGH");
	}

	@Test
	void 주문상품_조회() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 3L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		// when
		OrderResponse.OrderProducts foundOrderProduct = orderService.getOrderProducts(order.getId());

		// then
		assertThat(foundOrderProduct).isNotNull();
		assertThat(foundOrderProduct.orderId()).isEqualTo(order.getId());
		assertThat(foundOrderProduct.orderProducts()).isNotEmpty();
		assertThat(foundOrderProduct.orderProducts().size()).isEqualTo(1);
	}

	@Disabled("Payment 생성/결제 플로우 미구현")
	@Test
	void 주문상품_취소_성공__PAID_상태() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct1 = createOrderWithProducts(order, 3L);
		OrderProductEntity orderProduct2 = createOrderWithProducts(order, 2L);

		orderProduct1.updateStatus(OrderProductStatus.PENDING_APPROVE);
		orderProduct2.updateStatus(OrderProductStatus.PENDING_APPROVE);

		long beforeStock1 = inventoryRepository
			.findByProductIdOrThrow(orderProduct1.getProduct().getId())
			.getStock();
		long beforeStock2 = inventoryRepository
			.findByProductIdOrThrow(orderProduct2.getProduct().getId())
			.getStock();

		// TODO: payment

		// when
		orderService.cancelOrderProduct(user.getId(), orderProduct1.getId());
		orderService.cancelOrderProduct(user.getId(), orderProduct2.getId());

		// then
		OrderProductEntity canceled1 =
			orderProductRepository.findById(orderProduct1.getId()).orElseThrow();
		OrderProductEntity canceled2 =
			orderProductRepository.findById(orderProduct2.getId()).orElseThrow();

		assertThat(canceled1.getStatus()).isEqualTo(OrderProductStatus.CANCELED);
		assertThat(canceled2.getStatus()).isEqualTo(OrderProductStatus.CANCELED);

		long afterStock1 = inventoryRepository
			.findByProductIdOrThrow(orderProduct1.getProduct().getId())
			.getStock();
		long afterStock2 = inventoryRepository
			.findByProductIdOrThrow(orderProduct2.getProduct().getId())
			.getStock();

		assertThat(afterStock1).isEqualTo(beforeStock1 + 3L);
		assertThat(afterStock2).isEqualTo(beforeStock2 + 2L);
	}

	@Test
	void 주문_취소_실패__주문상품_배송중_상태() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.SHIPPING);

		// when & then
		assertThatThrownBy(() -> orderService.cancelOrderProduct(user.getId(), orderProduct.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_ALREADY_SHIPPED.name());
	}

	@Test
	void 주문_수정_성공__배송정보_변경() {
		// given
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

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
		UserEntity user = userRepository.save(UserEntityCreator.create());
		OrderEntity order = orderRepository.save(
			OrderEntity.create(ReceiverCreator.createReceiver(), user)
		);

		OrderProductEntity orderProduct = createOrderWithProducts(order, 2L);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		CourierEntity courier = courierRepository.save(
			CourierEntityCreator.createCourierEntity()
		);

		ShippingDetailEntity shippingDetail =
			ShippingDetailEntity.create(courier, orderProduct);

		shippingDetailRepository.save(shippingDetail);

		shippingDetail.startShipping();

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

}
