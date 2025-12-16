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
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
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

@DisplayName("주문 수정 - PUT /api/orders/{orderId}")
public class OrderUpdateTest extends MockMvcTest {

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
	@Autowired
	AccountRepository accountRepository;

	UserEntity testMember;

	ProductEntity testProduct;

	AddressEntity testAddress;
	SellerEntity testSeller;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		accountRepository.save(testSeller);

		testProduct = createProduct(category, testSeller);
		productRepository.save(testProduct);

		testAddress = addressRepository.save(AddressCreator.createAddress(testMember));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(testProduct.getId(), 1L, testSeller.getId())
		);
		orderService.createOrder(testMember.getEmail(), items, testAddress.getId());
	}

	@Test
	void 주문_수정_테스트__200_OK() throws Exception {
		OrderEntity savedOrder = orderRepository.findAll().stream().findFirst().orElseThrow();
		OrderRequest.Update request = new OrderRequest.Update(
			"수정된 Receiver",
			"010-9999-8888",
			"수정된 city",
			"수정된 district",
			"수정된 roadAddress",
			"수정된 detail"
		);

		ResultActions actions = mockMvc.perform(
			put("/api/orders/{orderId}", savedOrder.getId())
				.with(user(getMemberUserDetails(testMember.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)
				)
		);

		actions.andDo(print());
		actions.andExpect(status().isOk());
		assertThat(savedOrder).satisfies(order -> {
			assertThat(order.getReceiverVO().getName()).isEqualTo(request.receiverName());
			assertThat(order.getReceiverVO().getMobile()).isEqualTo(request.receiverMobile());
			assertThat(order.getReceiverVO().getCity()).isEqualTo(request.city());
			assertThat(order.getReceiverVO().getDistrict()).isEqualTo(request.district());
			assertThat(order.getReceiverVO().getRoad_address()).isEqualTo(request.roadAddress());
			assertThat(order.getReceiverVO().getDetail()).isEqualTo(request.detail());
		});
	}

	// TODO: OrderRequest.Update 필드 별로 검증(null, black) 테스트 추가 (에러 핸들러 추가 이후)
}
