package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.kt.common.UserEntityCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.constant.OrderSourceType;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.PaymentRepository;
import com.kt.repository.seller.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;

@DisplayName("주문 취소 - PATCH /api/orders/order-products/{orderProductId}/cancel")
public class OrderCancelTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderService orderService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	PaymentRepository paymentRepository;

	UserEntity testMember;
	ProductEntity testProduct;
	AddressEntity testAddress;
	SellerEntity testSeller;
	OrderProductEntity testOrderProduct;

	@BeforeEach
	void setUp() {
		testMember = UserEntityCreator.create();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = createProduct(category, testSeller);
		productRepository.save(testProduct);

		testAddress = addressRepository.save(AddressCreator.createAddress(testMember));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(
				testProduct.getId(),
				1L,
				testSeller.getId()
			)
		);
		OrderRequest orderRequest = new OrderRequest(
			items,
			testAddress.getId()
		);

		orderService.createOrder(
			testMember.getId(),
			orderRequest,
			OrderSourceType.DIRECT
		);

		OrderEntity order = orderRepository.findAll().stream()
			.findFirst()
			.orElseThrow();

		testOrderProduct = order.getOrderProducts().get(0);

		PaymentEntity payment = PaymentEntity.create(
			testProduct.getPrice(),
			3_000L,
			testOrderProduct
		);
		paymentRepository.save(payment);
	}

	@Test
	void 주문_취소_성공__모든_주문상품_PAID_상태() throws Exception {
		// when
		OrderEntity saved = orderRepository.findAll().stream().findFirst().orElseThrow();

		OrderProductEntity orderProduct = saved.getOrderProducts().get(0);
		orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/orders/order-products/{orderProductId}/cancel", orderProduct.getId())
				.with(user(getMemberUserDetails(testMember.getId())))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());

		OrderProductEntity canceled =
			orderProductRepository.findById(orderProduct.getId()).orElseThrow();

		assertThat(canceled.getStatus())
			.isEqualTo(OrderProductStatus.CANCELED);
	}

	@Test
	void 주문_취소_실패__하나라도_PAID_상태가_아님() throws Exception {
		OrderEntity saved = orderRepository.findAll().stream().findFirst().orElseThrow();

		saved.getOrderProducts().get(0)
		.updateStatus(OrderProductStatus.SHIPPING_READY);

		if(saved.getOrderProducts().size() > 1) {
			saved.getOrderProducts().subList(1, saved.getOrderProducts().size())
				.forEach(orderProduct ->
					orderProduct.updateStatus(OrderProductStatus.SHIPPING)
				);
		}

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/orders/order-products/{orderProductsId}/cancel", saved.getId())
				.with(user(getMemberUserDetails(testMember.getId())))
		);
		actions.andDo(print());
		actions.andExpect(status().isNotFound());

		OrderEntity notCanceled = orderRepository.findById(saved.getId()).orElseThrow();

		assertThat(notCanceled.getOrderProducts())
			.anySatisfy(orderProduct ->
				assertThat(orderProduct.getStatus())
					.isNotEqualTo(OrderProductStatus.CANCELED)
			);
	}
}
