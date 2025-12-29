package com.kt.api.cart;

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
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.*;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.*;
import com.kt.domain.dto.request.CartRequest;
import com.kt.repository.*;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.CartService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 수량 변경 - PATCH /api/carts/items/{cartItemId}")
public class CartUpdateTest extends MockMvcTest {

	@Autowired
	CartService cartService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CartRepository cartRepository;

	UserEntity user;
	CartItemEntity item;
	ProductEntity product;
	ProductEntity product2;

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

		product = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product);

		product2 = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product2);

		cartService.addItem(user.getId(), product.getId(), 1);
		item = cartService.getCart(user.getId()).getItems().get(0);
	}

	@Test
	void 장바구니_수량_변경_성공__200_OK() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartRequest.UpdateQuantity request =
			new CartRequest.UpdateQuantity(5);

		mockMvc.perform(
				patch("/api/carts/items/{cartItemId}", item.getId())
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk());
	}

	@Test
	void 장바구니_수량_변경_실패__타인의_아이템__404() throws Exception {
		UserEntity other = userRepository.save(UserEntityCreator.create());
		cartRepository.save(CartEntity.create(other));

		cartService.addItem(other.getId(), product2.getId(), 1);

		CartItemEntity otherItem =
			cartService.getCart(other.getId()).getItems().get(0);

		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartRequest.UpdateQuantity request =
			new CartRequest.UpdateQuantity(3);

		mockMvc.perform(
				patch("/api/carts/items/{cartItemId}", otherItem.getId())
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isNotFound());
	}
}
