package com.kt.api.cart;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.*;
import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.request.OrderRequest.CartOrderRequest;
import com.kt.domain.entity.*;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.CartService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 상품 선택 주문 생성 - POST /api/carts/orders")
class CartOrderCreateTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CartService cartService;
	@Autowired
	AddressRepository addressRepository;

	UserEntity user;
	ProductEntity product1;
	ProductEntity product2;
	CartItemEntity item1;
	CartItemEntity item2;
	AddressEntity address;

	@BeforeEach
	void setUp() {
		user = UserEntityCreator.create();
		userRepository.save(user);

		CartEntity cart = CartEntity.create(user);
		cartRepository.save(cart);

		SellerEntity seller = SellerEntityCreator.createSeller();
		sellerRepository.save(seller);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		product1 = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product1);

		product2 = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product2);

		address = AddressCreator.createAddress(user);
		addressRepository.save(address);

		cartService.addItem(user.getId(), product1.getId(), 1);
		cartService.addItem(user.getId(), product2.getId(), 2);

		CartEntity savedCart = cartService.getCart(user.getId());
		item1 = savedCart.getItems().get(0);
		item2 = savedCart.getItems().get(1);
		}

	@Test
	void 장바구니_상품_선택__주문_생성_성공() throws Exception {
		// given
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartOrderRequest request =
			new CartOrderRequest(
				List.of(item1.getId()),
				UUID.randomUUID()
			);

		// when
		mockMvc.perform(
				post("/api/carts/orders")
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(
						new OrderRequest.CartOrderRequest(
							List.of(item1.getId()),
							address.getId()
						)
					))
			)
			.andExpect(status().isOk());

		// then
		CartEntity afterCart =
			cartRepository.findByUserId(user.getId()).orElseThrow();

		assertThat(afterCart.getItems()).hasSize(1);
		assertThat(afterCart.getItems().get(0).getId())
			.isEqualTo(item2.getId());
	}
}