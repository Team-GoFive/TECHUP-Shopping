package com.kt.service;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.OrderEntityCreator.*;
import static com.kt.common.OrderProductCreator.*;
import static com.kt.common.ProductCreator.*;
import static com.kt.common.SellerEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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

import com.kt.common.SellerEntityCreator;
import com.kt.constant.OrderStatus;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.ReviewStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@SpringBootTest
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
	@Autowired
	AccountRepository accountRepository;

	OrderProductEntity testOrderProduct;
	UserEntity testUser;
	SellerEntity testSeller;

	@BeforeEach
	void setUp() throws Exception {
		testUser = createMember();
		userRepository.save(testUser);

		testSeller = createSeller();
		accountRepository.save(testSeller);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		OrderEntity order = createOrderEntity(testUser);
		orderRepository.save(order);

		ProductEntity product = createProduct(category, testSeller);
		productRepository.save(product);

		testOrderProduct = createOrderProduct(order, product, testSeller);
		orderProductRepository.save(testOrderProduct);

		order.getOrderProducts().add(testOrderProduct);
	}

	@Test
	void 리뷰생성_성공() {
		// given
		testOrderProduct.updateStatus(OrderProductStatus.PURCHASE_CONFIRMED);
		// when
		reviewService.create(testUser.getEmail(), testOrderProduct.getId(), "테스트리뷰내용");
		// // then
		Optional<ReviewEntity> saved = reviewRepository
			.findAll()
			.stream()
			.findFirst();

		assertThat(saved.isPresent()).isTrue();
		assertThat(saved.get().getContent()).isEqualTo("테스트리뷰내용");
	}

	@ParameterizedTest
	@EnumSource(
		value = OrderProductStatus.class,
		names = {"PURCHASE_CONFIRMED"},
		mode = EnumSource.Mode.EXCLUDE
	)
	void 리뷰생성_실패__주문_구매확정_아님(OrderProductStatus orderProductStatus) {
		// given
		testOrderProduct.updateStatus(orderProductStatus);

		// when and then
		assertThatThrownBy(
			() -> reviewService.create(
				testUser.getEmail(),
				testOrderProduct.getId(),
				"테스트리뷰내용"
			)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ORDER_NOT_CONFIRMED.name());
	}

	@Test
	void 리뷰변경_성공() {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		// when
		reviewService.update(testUser.getEmail(), review.getId(), "변경된테스트리뷰내용");

		// then
		Assertions.assertEquals("변경된테스트리뷰내용", review.getContent());
	}

	@Test
	void 리뷰삭제_성공() {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		// when
		reviewService.delete(testUser.getEmail(), review.getId());

		// then
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