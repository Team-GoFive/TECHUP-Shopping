package com.kt.api.seller.review;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
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
import com.kt.security.DefaultCurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static com.kt.common.CategoryEntityCreator.createCategory;
import static com.kt.common.ProductEntityCreator.createProduct;
import static com.kt.common.SellerEntityCreator.createSeller;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("판매자 상품별 리뷰 조회 - GET /api/seller/reviews/{productId}")
public class ReviewSearchByProductTest extends MockMvcTest {

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

	private SellerEntity testSeller;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private OrderEntity testOrder;
	private ProductEntity testProduct;
	private OrderProductEntity testOrderProduct;
	private ReviewEntity testReview;
	private DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		ReceiverVO receiverVO = ReceiverVO.create("test", "010-1234-5678", "city", "district", "road", "detail");
		testOrder = orderRepository.save(OrderEntity.create(receiverVO, testUser));

		testProduct = createProduct(testCategory, testSeller);
		productRepository.save(testProduct);

		testOrderProduct = orderProductRepository.save(
			OrderProductEntity.create(1L, testProduct.getPrice(), OrderProductStatus.PURCHASE_CONFIRMED, testOrder,
				testProduct));

		testReview = ReviewEntity.create("판매자 상품별 리뷰 내용");
		testReview.mapToOrderProduct(testOrderProduct);
		reviewRepository.save(testReview);
	}

	@Test
	void 판매자_상품별_리뷰_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/seller/reviews/{productId}", testProduct.getId())
				.with(user(sellerDetails))
				.param("page", "1")
				.param("size", "10"))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(1))
			.andExpect(jsonPath("$.data.list[0].reviewId").value(testReview.getId().toString()))
			.andExpect(jsonPath("$.data.list[0].content").value(testReview.getContent()));
	}
}
