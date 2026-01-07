package com.kt.api.cart;

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
import com.kt.domain.dto.request.CartRequest;
import com.kt.domain.entity.*;
import com.kt.repository.*;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 상품 추가 - POST /api/carts/items")
public class CartCreateTest extends MockMvcTest {

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
	ProductEntity product;

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
	}

	@Test
	void 장바구니_상품_추가_성공__200_OK() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartRequest.AddItem request =
			new CartRequest.AddItem(product.getId(), 3);

		mockMvc.perform(
				post("/api/carts/items")
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
	void 장바구니_상품_추가_실패__상품없음__404() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartRequest.AddItem request =
			new CartRequest.AddItem(UUID.randomUUID(), 1);

		mockMvc.perform(
				post("/api/carts/items")
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

	@Test
	void 장바구니_상품_추가_실패__수량_0__400() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		CartRequest.AddItem request =
			new CartRequest.AddItem(product.getId(), 0);

		mockMvc.perform(
				post("/api/carts/items")
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
			.andExpect(status().isBadRequest());
	}
}