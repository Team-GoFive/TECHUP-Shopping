package com.kt.api.refund;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import com.kt.domain.dto.request.RefundHistoryRequest;
import com.kt.domain.entity.*;
import com.kt.repository.*;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("환불 요청 - GET /api/refunds")
public class RefundCreateTest extends MockMvcTest {
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

	UserEntity testMember;
	OrderProductEntity testOrderProduct;

	@BeforeEach
	void setUp() {
		testMember = userRepository.save(UserEntityCreator.create());
		SellerEntity seller = sellerRepository.save(SellerEntityCreator.createSeller());
		CategoryEntity category = categoryRepository.save(CategoryEntityCreator.createCategory());
		ProductEntity product = productRepository.save(
			ProductEntityCreator.createProduct(category, seller)
		);

		OrderEntity order = orderRepository.save(
			OrderEntityCreator.createOrderEntity(testMember)
		);

		testOrderProduct = OrderProductEntity.create(
			1L,
			product.getPrice(),
			OrderProductStatus.SHIPPING_COMPLETED,
			order,
			product
		);
		order.addOrderProduct(testOrderProduct);
		orderProductRepository.save(testOrderProduct);

		paymentRepository.save(
			PaymentEntity.create(product.getPrice(), 3_000L, testOrderProduct)
		);
	}

	@Test
	void 환불_요청_성공__200_OK() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(testMember.getId(), testMember.getEmail(), AccountRole.MEMBER);

		RefundHistoryRequest request =
			new RefundHistoryRequest(testOrderProduct.getId(), "단순변심");

		ResultActions actions =
			mockMvc.perform(
				post("/api/refunds")
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request))
			);

		actions.andExpect(status().isOk());
	}
}
