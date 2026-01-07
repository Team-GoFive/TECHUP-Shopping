package com.kt.api.seller.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.SellerEntityCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("판매자 상품 상세 조회 - GET /api/seller/products/{productId}")
public class ProductDetailTest extends MockMvcTest {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private UserRepository userRepository;

	private SellerEntity testSeller;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());
	}

	@Test
	void 판매자_상품_상세_조회_성공__200_OK() throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
		inventoryRepository.save(inventory);

		// when
		ResultActions actions = mockMvc.perform(get("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.name").value(product.getName()));
	}

	@Test
	void 판매자_상품_상세_조회_실패__404_NotFound() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/seller/products/{productId}", UUID.randomUUID())
				.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isNotFound());
	}

	@Test
	void 판매자_상품_상세_조회_실패__다른_판매자_상품() throws Exception {
		// given
		SellerEntity otherSeller = createSeller("other@seller.com");
		sellerRepository.save(otherSeller);
		ProductEntity product = createProduct(testCategory, otherSeller);
		productRepository.save(product);

		// when
		ResultActions actions = mockMvc.perform(get("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isForbidden());
	}
}
