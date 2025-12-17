package com.kt.api.admin.order;

import static com.kt.common.CurrentUserCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("주문 취소(어드민) - get api/orders/order-products/{orderProductId}")
public class OrderCancelTest extends MockMvcTest {

	OrderEntity savedOrder;
	UserEntity savedUser;
	SellerEntity savedSeller;
	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@example.com",
		AccountRole.ADMIN
	);

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;

	@Test
	void 주문상품_취소__성공_200() throws Exception {
		//given
		UserEntity user = UserEntityCreator.createMember();
		savedUser = userRepository.save(user);

		OrderEntity order = OrderEntityCreator.createOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		SellerEntity seller = SellerEntityCreator.createSeller();
		savedSeller = sellerRepository.save(seller);

		CategoryEntity category = categoryRepository.save(
			CategoryEntityCreator.createCategory()
		);

		ProductEntity product = productRepository.save(
			ProductEntityCreator.createProduct(category, seller)
		);

		OrderProductEntity orderProduct = orderProductRepository.save(
			OrderProductEntity.create(
				1L,
				product.getPrice(),
				OrderProductStatus.SHIPPING_READY,
				order,
				product
			)
		);
		// when & then
		mockMvc.perform(patch("/api/admin/orders/order-products/{orderProductId}/cancel", orderProduct.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(user.getId())))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));
		OrderProductEntity canceled =
			orderProductRepository.findById(orderProduct.getId()).orElseThrow();

		assertThat(canceled.getStatus()).isEqualTo(OrderProductStatus.CANCELED);
	}

	@Test
	void 주문상품_취소__실패_NotFound_404() throws Exception {
		//given
		UserEntity user = UserEntityCreator.createMember();
		savedUser = userRepository.save(user);

		OrderEntity order = OrderEntityCreator.createOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		savedOrder.getOrderProducts()
			.forEach(orderProduct ->
				orderProduct.updateStatus(OrderProductStatus.SHIPPING_READY));

		mockMvc.perform(patch("/api/orders/order-products/{orderProductId}/cancel", UUID.randomUUID())
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(user.getId())))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isNotFound());
	}

}
