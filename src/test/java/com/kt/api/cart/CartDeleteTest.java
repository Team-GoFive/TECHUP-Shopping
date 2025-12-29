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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.*;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.*;
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
@DisplayName("장바구니 상품 삭제 - DELETE /api/carts/items/{cartItemId}")
public class CartDeleteTest extends MockMvcTest {

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

		cartService.addItem(user.getId(), product.getId(), 1);
		item = cartService.getCart(user.getId()).getItems().get(0);
	}

	@Test
	void 장바구니_상품_삭제_성공__200_OK() throws Exception {
		DefaultCurrentUser principal =
			new DefaultCurrentUser(
				user.getId(),
				user.getEmail(),
				AccountRole.MEMBER
			);

		ResultActions actions =
			mockMvc.perform(
				delete("/api/carts/items/{cartItemId}", item.getId())
					.with(authentication(
						new UsernamePasswordAuthenticationToken(
							principal,
							null,
							List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
					)
				))
				);
				actions.andExpect(status().isOk());
	}
}
