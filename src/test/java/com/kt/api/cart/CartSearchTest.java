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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
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
@DisplayName("장바구니 조회 - GET /api/cart")
public class CartSearchTest extends MockMvcTest {

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
	@Autowired
	CartService cartService;

	UserEntity testUser;
	ProductEntity product;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		CartEntity cart = CartEntity.create(testUser);
		cartRepository.save(cart);

		SellerEntity seller = SellerEntityCreator.createSeller();
		sellerRepository.save(seller);

		CategoryEntity category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		product = ProductEntityCreator.createProduct(category, seller);
		productRepository.save(product);

		cartService.addItem(testUser.getId(), product.getId(), 2);
	}

	@Test
	void 장바구니_조회_성공__200_OK() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				testUser.getId(),
				testUser.getEmail(),
				AccountRole.MEMBER
			);

		mockMvc.perform(
				get("/api/cart")
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
						)
					))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items.length()").value(1))
			.andExpect(jsonPath("$.items[0].quantity").value(2))
			.andExpect(jsonPath("$.totalAmount").value(2000));
	}

	@Test
	void 장바구니_조회_실패__인증_안됨() throws Exception {
		mockMvc.perform(get("/api/cart"))
			.andExpect(status().isUnauthorized());
	}

}
