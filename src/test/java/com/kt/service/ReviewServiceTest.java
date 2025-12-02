package com.kt.service;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderStatus;
import com.kt.constant.ReviewStatus;
import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReviewServiceTest {

	@Autowired
	ReviewService reviewService;

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
	UserEntity testUser;

	@BeforeEach
	void setUp() throws Exception {
		orderProductRepository.deleteAll();
		userRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();
		reviewRepository.deleteAll();

		testUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
		userRepository.save(testUser);

		ReceiverVO receiver = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);

		OrderEntity order = OrderEntity.create(
			receiver,
			testUser
		);
		orderRepository.save(order);

		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		ProductEntity product = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			category
		);
		productRepository.save(product);

		testOrderProduct = new OrderProductEntity(
			5L,
			5000L,
			OrderProductStatus.CREATED,
			order,
			product
		);
		orderProductRepository.save(testOrderProduct);
	}

	@Test
	void 리뷰생성_성공() {
		testOrderProduct.getOrder().updateStatus(OrderStatus.PURCHASE_CONFIRMED);
		reviewService.create(testOrderProduct.getId(), "테스트리뷰내용");

		ReviewEntity savedReview = reviewRepository
			.findAll()
			.stream()
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

		Assertions.assertEquals(testOrderProduct.getId(), savedReview.getOrderProduct().getId());
	}

	@ParameterizedTest
	@EnumSource(
		value = OrderStatus.class,
		names = {"PURCHASE_CONFIRMED"},
		mode = EnumSource.Mode.EXCLUDE
	)
	void 리뷰생성_실패__주문_구매확정_아님(OrderStatus orderStatus) {
		testOrderProduct.getOrder().updateStatus(orderStatus);

		Assertions.assertThrowsExactly(
			CustomException.class,
			() -> reviewService.create(testOrderProduct.getId(), "테스트리뷰내용")
		);
	}

	@Test
	void 리뷰변경_성공() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		reviewService.update("", review.getId(), "변경된테스트리뷰내용");

		Assertions.assertEquals("변경된테스트리뷰내용", review.getContent());
	}

	@Test
	void 리뷰삭제_성공() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);
		reviewService.delete("", review.getId());
		Assertions.assertEquals(ReviewStatus.REMOVED, review.getStatus());
	}

	@Test
	void 리뷰조회_성공() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		ReviewResponse.Search savedReviewDto = reviewService.getReview(testOrderProduct.getId());

		Assertions.assertEquals(review.getId(), savedReviewDto.reviewId());
	}

	@Test
	void 어드민_상품리뷰조회_성공() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ReviewResponse.Search> savedPage = reviewService.getReviewsByAdmin(pageRequest, null, null);

		ReviewResponse.Search savedReviewResponse = savedPage
			.stream()
			.findFirst()
			.orElse(null);

		Assertions.assertNotNull(savedReviewResponse);
		Assertions.assertEquals(review.getId(), savedReviewResponse.reviewId());
	}

	@Test
	void 내리뷰조회_성공() {
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.saveAndFlush(review);

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ReviewResponse.Search> savedPage = reviewService.getReviewsByUserId(pageRequest, testUser.getId());

		ReviewResponse.Search savedReviewResponse = savedPage
			.stream()
			.findFirst()
			.orElse(null);

		Assertions.assertNotNull(savedReviewResponse);
		Assertions.assertEquals(review.getId(), savedReviewResponse.reviewId());
		Assertions.assertEquals(review.getContent(), savedReviewResponse.content());
	}

}