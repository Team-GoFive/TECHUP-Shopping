package com.kt.api.review;

import static com.kt.common.CurrentUserCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductCreator;
import com.kt.common.ReceiverCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.ReviewStatus;
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

@DisplayName("상품 리뷰 삭제 - DELETE /api/reviews/{reviewId}")
public class ReviewDeleteTest extends MockMvcTest {

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

	UserEntity testMember;

	@BeforeEach
	void setUp() throws Exception {
		testMember = UserEntityCreator.createMember();
		userRepository.save(testMember);

		ReceiverVO receiver = ReceiverCreator.createReceiver();

		OrderEntity order = OrderEntity.create(receiver, testMember);
		orderRepository.save(order);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		testProduct = ProductCreator.createProduct(category);
		productRepository.save(testProduct);

		testOrderProduct = OrderProductCreator.createOrderProduct(order, testProduct);
		orderProductRepository.save(testOrderProduct);
	}

	@Test
	void 상품리뷰삭제_성공__200_OK() throws Exception {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/reviews/{reviewId}", review.getId())
				.with(user(getMemberUserDetails(testMember.getEmail())))
		);

		// then
		actions.andExpect(status().isOk());
		assertEquals(review.getStatus(), ReviewStatus.REMOVED);
	}
}
