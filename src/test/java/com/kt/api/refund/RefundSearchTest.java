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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.RefundStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.refund.RefundService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("환불 내역 조회 - GET /api/refunds")
public class RefundSearchTest extends MockMvcTest {
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
	RefundService refundService;

	UserEntity testMember;
	SellerEntity testSeller;
	OrderProductEntity testOrderProduct;
	ProductEntity testProduct;
	CategoryEntity testCategory;
	OrderEntity testOrder;

	@BeforeEach
	void setUp() {
		testMember = UserEntityCreator.create();
		userRepository.save(testMember);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = CategoryEntityCreator.createCategory();
		categoryRepository.save(testCategory);

		testProduct = ProductEntityCreator.createProduct(testCategory, testSeller);
		productRepository.save(testProduct);

		testOrder = OrderEntityCreator.createOrderEntity(testMember);
		orderRepository.save(testOrder);

		testOrderProduct = OrderProductEntity.create(
			1L,
			testProduct.getPrice(),
			OrderProductStatus.SHIPPING_COMPLETED,
			testOrder,
			testProduct
		);
		testOrder.addOrderProduct(testOrderProduct);
		orderProductRepository.save(testOrderProduct);

		PaymentEntity payment =
			PaymentEntity.create(
				testProduct.getPrice(),
				3_000L,
				testOrderProduct
			);
		paymentRepository.save(payment);

		refundService.requestRefund(
			testMember.getId(),
			testOrderProduct.getId(),
			"단순변심"
		);
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void 내_환불_목록_조회_성공__200_OK() throws Exception {
		DefaultCurrentUser memberPrincipal =
			new DefaultCurrentUser(
				testMember.getId(),
				testMember.getEmail(),
				AccountRole.MEMBER
			);
		// when
		ResultActions actions =
			mockMvc.perform(
				get("/api/refunds")
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							memberPrincipal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
					.param("page", "1")
					.param("size", "10")
			);

		// then
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.list.length()").value(1));
		actions.andExpect(jsonPath("$.data.list[0].status")
			.value(RefundStatus.REQUESTED.name()));
	}

	@Test
	void 셀러_환불요청_목록_조회_성공__200_OK() throws Exception {
		DefaultCurrentUser sellerPrincipal =
			new DefaultCurrentUser(
				testSeller.getId(),
				testSeller.getEmail(),
				AccountRole.SELLER
			);

		// when
		ResultActions actions =
			mockMvc.perform(
				get("/api/seller/refunds")
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							sellerPrincipal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
						)
					))
					.param("page", "1")
					.param("size", "10")
			);

		// then
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.list.length()").value(1));
		actions.andExpect(jsonPath("$.data.list[0].status")
			.value(RefundStatus.REQUESTED.name()));
	}
}