package com.kt.api.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("내가 작성한 리뷰 조회 - GET /api/users/reviews")
public class UserSearchReviewTest extends MockMvcTest {

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
	DefaultCurrentUser testMemberDetails;
	UserEntity testUser;

	@BeforeEach
	void setUp() throws Exception {
		testUser  = UserEntityCreator.createMember();
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
	void 내리뷰조회_성공__200_OK() throws Exception {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.saveAndFlush(review);

		// when
		ResultActions Actions = mockMvc.perform(
			get("/api/users/reviews")
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
				.param("page","1")
				.param("size","10")
		).andDo(print());

		// then
		Actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(1))
			.andExpect(jsonPath("$.data.list[0].reviewId").value(review.getId().toString()))
			.andExpect(jsonPath("$.data.list[0].content").value(review.getContent().toString()));
	}

	@Test
	void 내리뷰조회_실패__다른사용자() throws Exception {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.saveAndFlush(review);

		// when
		ResultActions Actions = mockMvc.perform(
			get("/api/users/reviews")
				.with(user(CurrentUserCreator.getMemberUserDetails()))
				.param("page","1")
				.param("size","10")
		).andDo(print());

		// then
		Actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(0));
	}
}
