package com.kt.service.seller;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.response.SellerReviewResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.seller.review.SellerReviewService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("판매자 리뷰 서비스 테스트")
class SellerReviewServiceTest {

	@Autowired
	private SellerReviewService sellerReviewService;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;

	private OrderProductEntity testOrderProduct;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private SellerEntity testSeller;
	private ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = CategoryEntityCreator.createCategory();
		categoryRepository.save(testCategory);

		OrderEntity order = createOrderEntity(testUser);
		orderRepository.save(order);

		testProduct = ProductCreator.createProduct(testCategory, testSeller);
		productRepository.save(testProduct);

		testOrderProduct = OrderProductCreator.createOrderProduct(order, testProduct, testSeller);
		orderProductRepository.save(testOrderProduct);

		order.getOrderProducts().add(testOrderProduct);
	}

	private OrderEntity createOrderEntity(UserEntity user) {
		ReceiverVO receiverVO = ReceiverVO.create("test", "010-1234-5678", "city", "district", "road", "detail");
		return OrderEntity.create(receiverVO, user);
	}

	@Test
	void 판매자_모든_리뷰_조회_성공() {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<SellerReviewResponse.Search> result = sellerReviewService.getAllReviews(pageable, testSeller.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).reviewId()).isEqualTo(review.getId());
	}

	@Test
	void 판매자_상품별_리뷰_조회_성공() {
		// given
		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(review);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<SellerReviewResponse.Search> result = sellerReviewService.getReviewsByProduct(pageable, testSeller.getId(),
			testProduct.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).reviewId()).isEqualTo(review.getId());
	}
}
