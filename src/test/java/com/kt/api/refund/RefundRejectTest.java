package com.kt.api.refund;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.*;
import com.kt.constant.*;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.domain.entity.*;
import com.kt.repository.*;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.refund.RefundHistoryRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.refund.RefundService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("환불 거절 - PATCH /api/seller/refunds/{id}/reject")
class RefundRejectTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	RefundHistoryRepository refundRepository;
	@Autowired
	RefundService refundService;

	SellerEntity seller;
	UUID refundId;

	@BeforeEach
	void setUp() {
		UserEntity member = userRepository.save(UserEntityCreator.create());
		seller = sellerRepository.save(SellerEntityCreator.createSeller());

		CategoryEntity category = categoryRepository.save(CategoryEntityCreator.createCategory());
		ProductEntity product = productRepository.save(
			ProductEntityCreator.createProduct(category, seller)
		);

		OrderEntity order = orderRepository.save(
			OrderEntityCreator.createOrderEntity(member)
		);

		OrderProductEntity orderProduct = OrderProductEntity.create(
			1L,
			product.getPrice(),
			OrderProductStatus.SHIPPING_COMPLETED,
			order,
			product
		);
		order.addOrderProduct(orderProduct);
		orderProductRepository.save(orderProduct);

		paymentRepository.save(
			PaymentEntity.create(product.getPrice(), 3_000L, orderProduct)
		);

		refundService.requestRefund(member.getId(), orderProduct.getId(), "단순변심");
		refundId = refundRepository.findAll().get(0).getId();
	}

	@Test
	void 환불_거절_성공__200_OK() throws Exception {
		DefaultCurrentUser sellerPrincipal =
			new DefaultCurrentUser(seller.getId(), seller.getEmail(), AccountRole.SELLER);

		RefundRejectRequest request =
			new RefundRejectRequest("상품 훼손");

		ResultActions actions =
			mockMvc.perform(
				patch("/api/seller/refunds/{refundId}/reject", refundId)
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							sellerPrincipal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
						)
					))
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request))
			);

		actions.andExpect(status().isOk());
	}
}
