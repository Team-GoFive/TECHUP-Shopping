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

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.refund.RefundHistoryRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.RefundService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("환불 승인 - PATCH /api/seller/refunds/{refundId}/approve")
public class RefundConfirmTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	InventoryRepository inventoryRepository;
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

		InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
		inventoryRepository.save(inventory);

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
	void 환불_승인_성공__200_OK() throws Exception {
		DefaultCurrentUser sellerPrincipal =
			new DefaultCurrentUser(seller.getId(), seller.getEmail(), AccountRole.SELLER);

		ResultActions actions =
			mockMvc.perform(
				patch("/api/seller/refunds/{refundId}/approve", refundId)
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							sellerPrincipal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
						)
					))
			);

		actions.andExpect(status().isOk());
	}
}