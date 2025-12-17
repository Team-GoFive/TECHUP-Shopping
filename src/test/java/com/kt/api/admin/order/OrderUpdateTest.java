package com.kt.api.admin.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.OrderProductCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.request.OrderProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;

@DisplayName("주문상품 상태 강제 변경(어드민) - PATCH /api/admin/orders/order-products/{orderProductId}/force-change-status")
public class OrderUpdateTest extends MockMvcTest {

	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SellerRepository sellerRepository;

	private OrderProductEntity givenShippingReadyOrderProduct() {
		CategoryEntity category = categoryRepository.save(
			CategoryEntityCreator.createCategory()
		);

		SellerEntity seller = sellerRepository.save(
			SellerEntityCreator.createSeller()
		);

		ProductEntity product = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product);

		OrderEntity order = OrderEntityCreator.createOrderEntity();
		orderRepository.save(order);

		OrderProductEntity orderProduct =
			OrderProductCreator.createOrderProduct(order, product, seller);

		orderProduct.updateStatus(OrderProductStatus.SHIPPING_READY);
		return orderProductRepository.save(orderProduct);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 주문상품_상태_강제변경_성공_200() throws Exception {

		// given
		OrderProductEntity orderProduct = givenShippingReadyOrderProduct();

		OrderProductRequest.ForceChangeStatus request =
			new OrderProductRequest.ForceChangeStatus(
				OrderProductStatus.SHIPPING
			);

		// when & then
		mockMvc.perform(
				patch("/api/admin/orders/order-products/{orderProductId}/force-change-status",
					orderProduct.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(print())
			.andExpect(status().isOk());

	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 주문상품_상태_강제변경_실패__잘못된_상태전이_400_BadRequest() throws Exception {
		// given
		OrderProductEntity orderProduct = givenShippingReadyOrderProduct();

		OrderProductRequest.ForceChangeStatus request =
			new OrderProductRequest.ForceChangeStatus(
				OrderProductStatus.PURCHASE_CONFIRMED
			);

		// when & then
		mockMvc.perform(
				patch("/api/admin/orders/order-products/{orderProductId}/force-change-status",
					orderProduct.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isBadRequest());
	}

@Test
@WithMockUser(roles = "MEMBER")
void 주문상품_상태_강제변경_API_실패__권한_없음() throws Exception {
	// given
	OrderProductEntity orderProduct = givenShippingReadyOrderProduct();

	OrderProductRequest.ForceChangeStatus request =
		new OrderProductRequest.ForceChangeStatus(
			OrderProductStatus.SHIPPING
		);

	// when & then
	mockMvc.perform(
			patch("/api/admin/orders/order-products/{orderProductId}/force-change-status",
				orderProduct.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		)
		.andExpect(status().isForbidden());
}

}
