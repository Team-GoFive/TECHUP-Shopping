package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;

@DisplayName("주문 취소 - PATCH /api/orders/{orderId}/cancel")
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
	AddressRepository addressRepository;

	UserEntity testMember;

	ProductEntity testProduct;

	AddressEntity testAddress;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testProduct = createProduct(category);
		productRepository.save(testProduct);

		testAddress = addressRepository.save(AddressCreator.createAddress(testMember));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(testProduct.getId(), 1L)
		);
		orderService.createOrder(testMember.getEmail(), items, testAddress.getId());
	}

	@Test
	void 주문_취소_성공__모든_주문상품_PAID_상태() throws Exception {
		// when
		OrderEntity saved = orderRepository.findAll().stream().findFirst().orElseThrow();

		saved.getOrderProducts().forEach(orderProduct ->
			orderProduct.updateStatus(OrderProductStatus.PAID)
		);

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/orders/{orderId}/cancel", saved.getId())
				.with(user(getMemberUserDetails(testMember.getId())))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());

		OrderEntity canceled = orderRepository.findById(saved.getId()).orElseThrow();

		assertThat(canceled.getOrderProducts())
			.allSatisfy(orderProduct ->
				assertThat(orderProduct.getStatus())
					.isEqualTo(OrderProductStatus.CANCELED)
			);
	}

	@Test
	void 주문_취소_실패__하나라도_PAID_상태가_아님() throws Exception {
		OrderEntity saved = orderRepository.findAll().stream().findFirst().orElseThrow();

		saved.getOrderProducts().get(0)
		.updateStatus(OrderProductStatus.PAID);

		if(saved.getOrderProducts().size() > 1) {
			saved.getOrderProducts().subList(1, saved.getOrderProducts().size())
				.forEach(orderProduct ->
					orderProduct.updateStatus(OrderProductStatus.SHIPPING)
				);
		}

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/orders/{orderId}/cancel", saved.getId())
				.with(user(getMemberUserDetails(testMember.getId())))
		);
		actions.andDo(print());
		actions.andExpect(status().isBadRequest());

		OrderEntity notCanceled = orderRepository.findById(saved.getId()).orElseThrow();

		assertThat(notCanceled.getOrderProducts())
			.anySatisfy(orderProduct ->
				assertThat(orderProduct.getStatus())
					.isNotEqualTo(OrderProductStatus.CANCELED)
			);
	}
}
