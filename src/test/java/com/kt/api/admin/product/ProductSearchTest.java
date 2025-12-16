package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.UUID;

import com.kt.constant.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 조회 (어드민) - GET /api/admin/products")
public class ProductSearchTest extends MockMvcTest {
	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;
	@Autowired
	AccountRepository accountRepository;

	CategoryEntity testCategory;
	SellerEntity testSeller;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@test.com",
		AccountRole.ADMIN
	);

	ArrayList<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		accountRepository.save(testSeller);

		products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory, testSeller));
		}

		for (int i = 0; i < 5; i++) {
			ProductEntity product = createProduct(testCategory, testSeller);
			product.inActivate();
			products.add(product);
		}

		productRepository.saveAll(products);
	}

	@Test
	void 어드민은_상품상태_관계없이_모두_조회__200_OK() throws Exception {
		ResultActions actions = mockMvc.perform(
			get("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "1")
				.param("size", "10")
		);

		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.list.length()").value(10))
			.andDo(print());
	}
}
