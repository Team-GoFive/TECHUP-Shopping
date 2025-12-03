package com.kt.api.review;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderStatus;
import com.kt.domain.dto.request.ReviewRequest;
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

@DisplayName("상품 리뷰 작성 - POST /api/orderproducts/{orderProductId}/reviews")
public class ReviewCreateTest extends MockMvcTest {

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

	OrderProductEntity testOrderProduct;
	ProductEntity testProduct;

	@BeforeEach
	void setUp() throws Exception {
		UserEntity user = UserEntityCreator.createMember();
		userRepository.save(user);

		ReceiverVO receiver = ReceiverCreator.createReceiver();

		OrderEntity order = OrderEntity.create(receiver, user);
		orderRepository.save(order);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		testProduct = ProductCreator.createProduct(category);
		productRepository.save(testProduct);

		testOrderProduct = OrderProductCreator.createOrderProduct(order, testProduct);
		orderProductRepository.save(testOrderProduct);
	}

	@Test
	void 상품리뷰작성_성공__200_OK() throws Exception {
		// given
		testOrderProduct.getOrder().updateStatus(OrderStatus.PURCHASE_CONFIRMED);
		ReviewRequest.Create reviewCreate = new ReviewRequest.Create(
			testOrderProduct.getId(),
			"생성한테스트리뷰내용"
		);
		String json = objectMapper.writeValueAsString(reviewCreate);

		// when
		ResultActions actions = mockMvc.perform(
			post("/api/orderproducts/{orderProductId}/reviews", testOrderProduct.getId())
				.with(user("wjd123@naver.com"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		);

		// then
		actions.andExpect(status().isOk());
		ReviewEntity savedReview = reviewRepository.findByOrderProductIdOrThrow(testOrderProduct.getId());
		Assertions.assertNotNull(savedReview);
		Assertions.assertEquals(savedReview.getContent(), reviewCreate.content());
	}
}
