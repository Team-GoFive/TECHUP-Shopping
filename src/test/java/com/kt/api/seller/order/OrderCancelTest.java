package com.kt.api.seller.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("판매자 주문 상품 취소 - PATCH /api/seller/orders/{orderProductId}/cancel")
public class OrderCancelTest extends MockMvcTest {

	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
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
		testUser = UserEntityCreator.create();
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
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
		inventoryRepository.save(inventory);
		testOrderProduct = orderProductRepository.save(
			OrderProductEntity.create(1L, product.getPrice(), OrderProductStatus.CREATED, testOrder, product));
	}

	@Test
	void 판매자_주문_상품_취소_성공__200_OK() throws Exception {
		// given
		testOrderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);
		orderProductRepository.save(testOrderProduct);

		// when
		ResultActions actions = mockMvc.perform(
				patch("/api/seller/orders/{orderProductId}/cancel", testOrderProduct.getId())
					.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		OrderProductEntity cancelledOrderProduct = orderProductRepository.findById(testOrderProduct.getId()).orElseThrow();
		assertThat(cancelledOrderProduct.getStatus()).isEqualTo(OrderProductStatus.CANCELED);
	}
}
