package com.kt.api.seller.order;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
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

@DisplayName("판매자 주문 상품 목록 조회 - GET /api/seller/orders")
public class OrderSearchTest extends MockMvcTest {

	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;

	private SellerEntity testSeller;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private OrderEntity testOrder;
	private OrderProductEntity testOrderProduct1;
	private OrderProductEntity testOrderProduct2;
	private DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		ReceiverVO receiverVO = ReceiverVO.create(
			"test", "010-1234-5678", "city", "district", "road", "detail"
		);
		testOrder = orderRepository.save(OrderEntity.create(receiverVO, testUser));

		ProductEntity product1 = createProduct(testCategory, testSeller);
		productRepository.save(product1);
		testOrderProduct1 = orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				product1.getPrice(),
				OrderProductStatus.CREATED,
				testOrder,
				product1
			)
		);

		ProductEntity product2 = createProduct(testCategory, testSeller);
		productRepository.save(product2);
		testOrderProduct2 = orderProductRepository.save(
			OrderProductEntity.create(
				2L,
				product2.getPrice(),
				OrderProductStatus.SHIPPING,
				testOrder,
				product2
			)
		);
	}

	@Test
	void 판매자_주문_상품_목록_조회_성공__200_OK() throws Exception {

		// given
		ResultActions actions = mockMvc.perform(
				get("/api/seller/orders")
					.with(user(sellerDetails))
					.param("page", "1")
					.param("size", "10")
			)
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(2));
	}

	@Test
	void 판매자_주문_상품_목록_조회_필터링__orderProductId_성공() throws Exception {

		// given
		ResultActions actions = mockMvc.perform(
				get("/api/seller/orders")
					.with(user(sellerDetails))
					.param("page", "1")
					.param("size", "10")
					.param("orderProductId", testOrderProduct1.getId().toString())
			)
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(1))
			.andExpect(jsonPath("$.data.list[0].orderProductId")
				.value(testOrderProduct1.getId().toString()));
	}

	@Test
	void 판매자_주문_상품_목록_조회_필터링__status_성공() throws Exception {

		// given

		ResultActions actions = mockMvc.perform(
				get("/api/seller/orders")
					.with(user(sellerDetails))
					.param("page", "1")
					.param("size", "10")
					.param("status", OrderProductStatus.SHIPPING.name())
			)
			.andDo(print());

		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(1))
			.andExpect(jsonPath("$.data.list[0].orderProductId")
				.value(testOrderProduct2.getId().toString()))
			.andExpect(jsonPath("$.data.list[0].status")
				.value(OrderProductStatus.SHIPPING.name()));
	}

	@Test
	void 판매자_주문_상품_목록_조회_실패__다른_판매자_상품() throws Exception {

		// given

		SellerEntity otherSeller = createSeller();
		sellerRepository.save(otherSeller);

		ProductEntity otherProduct = createProduct(testCategory, otherSeller);
		productRepository.save(otherProduct);

		OrderProductEntity otherOrderProduct = orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				otherProduct.getPrice(),
				OrderProductStatus.CREATED,
				testOrder,
				otherProduct
			)
		);

		// then

		ResultActions actions = mockMvc.perform(
				get("/api/seller/orders")
					.with(user(sellerDetails))
					.param("page", "1")
					.param("size", "10")
					.param("orderProductId", otherOrderProduct.getId().toString())
			)
			.andDo(print());

		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(0));
	}
}
