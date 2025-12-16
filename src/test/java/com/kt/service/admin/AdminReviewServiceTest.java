package com.kt.service.admin;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.OrderEntityCreator.*;
import static com.kt.common.OrderProductCreator.*;
import static com.kt.common.ProductCreator.*;
import static com.kt.common.UserEntityCreator.*;

import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.seller.SellerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.ReviewStatus;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AdminReviewServiceTest {

	@Autowired
	AdminReviewService adminReviewService;

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

	OrderProductEntity testOrderProduct;
	UserEntity testUser;
	SellerEntity testSeller;

	@BeforeEach
	void setUp() throws Exception {
		testUser = createMember();
		userRepository.save(testUser);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		OrderEntity order = createOrderEntity(testUser);
		orderRepository.save(order);

		ProductEntity product = createProduct(category, testSeller);
		productRepository.save(product);

		testOrderProduct = createOrderProduct(order, product, testSeller);
		orderProductRepository.save(testOrderProduct);

		order.getOrderProducts().add(testOrderProduct);
	}

	@Test
	void 리뷰삭제_성공_어드민() {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		// when
		adminReviewService.delete(testUser.getEmail(), review.getId());

		// then
		Assertions.assertEquals(ReviewStatus.REMOVED, review.getStatus());
	}

	@Test
	void 리뷰조회_성공_어드민() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		ReviewResponse.Search savedReviewDto = adminReviewService.getReview(testOrderProduct.getId());

		Assertions.assertEquals(review.getId(), savedReviewDto.reviewId());
	}

	@Test
	void 상품리뷰조회_성공_어드민() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ReviewResponse.Search> savedPage = adminReviewService.getReviewsByAdmin(pageRequest, null, null);

		ReviewResponse.Search savedReviewResponse = savedPage
			.stream()
			.findFirst()
			.orElse(null);

		Assertions.assertNotNull(savedReviewResponse);
		Assertions.assertEquals(review.getId(), savedReviewResponse.reviewId());
	}

}