package com.kt.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.UserRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReviewRepositoryTest {

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

	@BeforeEach
	void setUp() throws Exception {
		orderProductRepository.deleteAll();
		userRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();
		reviewRepository.deleteAll();

		UserEntity user = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
		userRepository.save(user);

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
			user
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
	void 주문상품번호_으로_리뷰_조회_성공() {
		ReviewEntity review = ReviewEntity.create(
			"테스트리뷰내용"
		);
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		ReviewEntity savedReview = reviewRepository
			.findByOrderProductIdOrThrow(testOrderProduct.getId());

		Assertions.assertEquals(review.getId(), savedReview.getId());
	}
}