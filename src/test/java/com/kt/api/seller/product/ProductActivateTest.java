package com.kt.api.seller.product;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.ProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static com.kt.common.CategoryEntityCreator.createCategory;
import static com.kt.common.ProductEntityCreator.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@DisplayName("판매자 상품 활성화 - PATCH /api/seller/products/{productId}/activate")
public class ProductActivateTest extends MockMvcTest {

	@Autowired
	private ProductRepository productRepository;
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
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());
	}

	@Test
	void 상품_활성화_성공__200_OK() throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		product.inActivate();
		productRepository.save(product);

		// when
		ResultActions actions = mockMvc.perform(patch("/api/seller/products/{productId}/activate", product.getId())
				.with(user(sellerDetails)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		ProductEntity activatedProduct = productRepository.findById(product.getId()).orElseThrow();
		assertThat(activatedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}
}
