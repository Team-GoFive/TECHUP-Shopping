package com.kt.api.review;

import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;

import static com.kt.common.CurrentUserCreator.getMemberUserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.kt.domain.dto.request.ReviewRequest;
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
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;

@DisplayName("상품 리뷰 수정 - PATCH /api/reviews/{reviewId}")
public class ReviewUpdateTest extends MockMvcTest {

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
	@Autowired
	SellerRepository sellerRepository;

	UserEntity testMember;
	OrderProductEntity testOrderProduct;
	ProductEntity testProduct;
	SellerEntity testSeller;

	@BeforeEach
	void setUp() throws Exception {
		testMember = UserEntityCreator.createMember();
		userRepository.save(testMember);

		ReceiverVO receiver = ReceiverCreator.createReceiver();

		OrderEntity order = OrderEntity.create(receiver, testMember);
		orderRepository.save(order);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = ProductCreator.createProduct(category, testSeller);
		productRepository.save(testProduct);

		testOrderProduct = OrderProductCreator.createOrderProduct(order, testProduct, testSeller);
		orderProductRepository.save(testOrderProduct);
	}

	@Test
	void 상품리뷰수정_성공__200_OK() throws Exception {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		ReviewRequest.Update update = new ReviewRequest.Update(
			"변경된 리뷰 내용"
		);
		String updateJson = objectMapper.writeValueAsString(update);

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/reviews/{reviewId}", review.getId())
				.with(user(getMemberUserDetails(testMember.getEmail())))
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson)
		);

		// then
		actions.andExpect(status().isOk());
		assertEquals(review.getContent(), update.content());
	}
}
