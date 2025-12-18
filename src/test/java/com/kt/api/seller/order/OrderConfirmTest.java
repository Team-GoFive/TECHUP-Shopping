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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("판매자 주문 상품 취소 - PATCH /api/seller/orders/{orderProductId}/confirm")
public class OrderConfirmTest extends MockMvcTest {
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
	private OrderProductEntity testOrderProduct;
	private DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		ReceiverVO receiverVO = ReceiverVO.create("test", "010-1234-5678", "city", "district", "road", "detail");
		testOrder = orderRepository.save(OrderEntity.create(receiverVO, testUser));

		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);
		testOrderProduct = orderProductRepository.save(
			OrderProductEntity.create(1L, product.getPrice(), OrderProductStatus.PAID, testOrder, product));
	}

	@Test
	void 판매자_주문_상품_배송_대기중으로_변경__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
				patch("/api/seller/orders/{orderProductId}/confirm", testOrderProduct.getId())
					.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		OrderProductEntity confirmedOrderProduct = orderProductRepository.findByIdOrThrow(testOrderProduct.getId());
		assertThat(confirmedOrderProduct.getStatus()).isEqualTo(OrderProductStatus.SHIPPING_READY);

	}
}
