package com.kt.api.review.admin;

import com.kt.common.AdminCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.AdminEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.admin.AdminRepository;
import com.kt.repository.seller.SellerRepository;

import static org.junit.jupiter.api.Assertions.*;
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

@DisplayName("상품 리뷰 삭제 (어드민) - DELETE /api/admin/reviews/{reviewId}")
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
	AdminRepository adminRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SellerRepository sellerRepository;

	OrderProductEntity testOrderProduct;
	ProductEntity testProduct;

	AdminEntity testAdmin;
	SellerEntity testSeller;
	UserEntity testUser;
	@BeforeEach
	void setUp() throws Exception {
		testAdmin = AdminCreator.create();
		testUser = UserEntityCreator.create();
		adminRepository.save(testAdmin);
		userRepository.save(testUser);
		ReceiverVO receiver = ReceiverCreator.createReceiver();

		OrderEntity order = OrderEntity.create(receiver, testUser);
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
	void 상품리뷰삭제_성공() throws Exception {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/reviews/{reviewId}", review.getId())
				.with(user(CurrentUserCreator.getAdminUserDetails(testAdmin.getEmail())))
		).andDo(print());

		// then
		actions.andExpect(status().isOk());
		assertEquals(ReviewStatus.REMOVED, review.getStatus());
	}
}
