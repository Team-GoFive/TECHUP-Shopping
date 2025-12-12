package com.kt.api.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@DisplayName("내 리뷰 가능한 주문상품 조회 - GET /api/users/reviewable-products")
public class UserSearchReviewableTest extends MockMvcTest {
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	CategoryRepository categoryRepository;

	OrderEntity testOrder;
	OrderProductEntity testOrderProduct;
	ProductEntity testProduct;
	UserEntity testUser;

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		ReceiverVO receiver = ReceiverCreator.createReceiver();

		testOrder = OrderEntity.create(receiver, testUser);
		orderRepository.save(testOrder);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		testProduct = ProductCreator.createProduct(category);
		productRepository.save(testProduct);

		testOrderProduct = OrderProductCreator.createOrderProduct(testOrder, testProduct);
		orderProductRepository.save(testOrderProduct);
	}


	@Test
	void 주문상품조회_성공__200_OK() throws Exception {
		// given
		testOrder.updateStatus(OrderStatus.PURCHASE_CONFIRMED);
		orderRepository.save(testOrder);

		// when
		ResultActions Actions = mockMvc.perform(
			get("/api/users/reviewable-products")
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
				.param("page","1")
				.param("size","10")
		).andDo(print());

		// then
		Actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(1))
			.andExpect(jsonPath("$.data.list[0].orderProductId").value(testOrderProduct.getId().toString()));
	}


	@Test
	void 주문상품조회_실패__작성된리뷰존재_주문상품없음() throws Exception {
		// given
		testOrder.updateStatus(OrderStatus.PURCHASE_CONFIRMED);
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.saveAndFlush(review);

		// when
		ResultActions Actions = mockMvc.perform(
			get("/api/users/reviewable-products")
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
				.param("page","1")
				.param("size","10")
		).andDo(print());

		// then
		Actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(0));
	}

	@Test
	void 주문상품조회_실패__주문상태구매확정아님_주문상품없음() throws Exception {
		// given
		testOrder.updateStatus(OrderStatus.CANCELED);
		orderRepository.save(testOrder);

		// when
		ResultActions Actions = mockMvc.perform(
			get("/api/users/reviewable-products")
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
				.param("page","1")
				.param("size","10")
		).andDo(print());

		// then
		Actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(0));
	}
}
