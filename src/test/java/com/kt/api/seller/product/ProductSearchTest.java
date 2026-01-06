package com.kt.api.seller.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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

@DisplayName("판매자 상품 목록 조회 - GET /api/seller/products")
public class ProductSearchTest extends MockMvcTest {

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

	private List<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory, testSeller));
		}
		for (int i = 0; i < 5; i++) {
			ProductEntity inactiveProduct = createProduct(testCategory, testSeller);
			inactiveProduct.inActivate();
			products.add(inactiveProduct);
		}
		productRepository.saveAll(products);
		List<InventoryEntity> inventories = new ArrayList<>();
		for (ProductEntity product : products) {
			InventoryEntity inventory = InventoryEntity.create(product.getId(), 1000L);
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);
	}

	@Test
	void 판매자_상품_목록_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/seller/products")
				.with(user(sellerDetails))
				.param("page", "1")
				.param("size", "10"))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.data.list.length()").value(10));
	}

}
