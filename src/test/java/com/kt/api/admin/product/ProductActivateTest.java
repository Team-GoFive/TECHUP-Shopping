package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

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
import com.kt.constant.ProductStatus;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 활성화 (어드민) - PUT /api/admin/products/{productId}/activate")
public class ProductActivateTest extends MockMvcTest {

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

	ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		accountRepository.save(testSeller);

		testProduct = createProduct(testCategory, testSeller);
		testProduct.inActivate();
		productRepository.save(testProduct);
	}

	@Test
	void 상품_활성화_성공__200_OK() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
			patch("/api/admin/products/{productId}/activate", testProduct.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
		);

		// then
		actions.andExpect(status().isOk());
		ProductEntity saved = productRepository.findByIdOrThrow(testProduct.getId());
		assertThat(saved.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}
}
