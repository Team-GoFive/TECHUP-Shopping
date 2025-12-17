package com.kt.api.seller.product;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.ProductStatus;
import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.kt.common.CategoryEntityCreator.createCategory;
import static com.kt.common.ProductEntityCreator.createProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@DisplayName("판매자 상품 다중 품절 처리 - PATCH /api/seller/products/sold-out")
public class ProductSoldOutTest extends MockMvcTest {

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

	private List<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		products = new java.util.ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory, testSeller));
		}
		productRepository.saveAll(products);
	}

	@Test
	void 상품_품절_처리_성공__200_OK() throws Exception {
		// given
		SellerProductRequest.SoldOut request = new SellerProductRequest.SoldOut(
			products.stream().map(ProductEntity::getId).toList()
		);

		// when
		ResultActions actions = mockMvc.perform(patch("/api/seller/products/sold-out")
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		products.forEach(product -> {
			ProductEntity updatedProduct = productRepository.findById(product.getId()).orElseThrow();
			Assertions.assertEquals(ProductStatus.IN_ACTIVATED, updatedProduct.getStatus());
		});
	}
}
