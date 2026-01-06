package com.kt.service.admin;

import static com.kt.common.SellerEntityCreator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.AccountRole;
import com.kt.constant.ProductStatus;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminProductServiceTest {

	private final AdminProductService adminProductService;
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final CategoryRepository categoryRepository;
	private final OrderProductRepository orderProductRepository;
	private final SellerRepository sellerRepository;
	private SellerEntity testSeller;

	@Autowired
	AdminProductServiceTest(AdminProductService adminProductService, ProductRepository productRepository,
		InventoryRepository inventoryRepository,
		CategoryRepository categoryRepository, OrderProductRepository orderProductRepository,
		SellerRepository sellerRepository) {
		this.inventoryRepository = inventoryRepository;
		this.orderProductRepository = orderProductRepository;
		this.adminProductService = adminProductService;
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.sellerRepository = sellerRepository;
	}

	@BeforeEach
	void setUp() {
		testSeller = createSeller();
		sellerRepository.save(testSeller);
	}

	@Test
	void 상품_삭제() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		ProductEntity product = ProductEntity.create(
			"상품1",
			1000L,
			category,
			testSeller
		);
		productRepository.save(product);

		// when
		adminProductService.delete(product.getId());

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.DELETED);
	}

	@Test
	void 상품_목록_조회() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		List<ProductEntity> products = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				category,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

		List<InventoryEntity> inventories = new ArrayList<>();
		for (ProductEntity product : products) {
			InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);

		// when
		PageRequest pageRequest = PageRequest.of(1, 10);
		Page<ProductResponse.Search> search = adminProductService.search(AccountRole.ADMIN, null, null, pageRequest);

		// then
		assertThat(search.getTotalElements()).isEqualTo(20);
		assertThat(search.getTotalPages()).isEqualTo(2);
		assertThat(search.getContent().size()).isEqualTo(10);
	}

	@Test
	void 상품_목록_조회__검색_카테고리명() {
		// given
		CategoryEntity categoryDog = CategoryEntity.create("강아지", null);
		categoryRepository.save(categoryDog);
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);

		List<ProductEntity> products = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				categoryDog,
				testSeller
			);
			products.add(product);
		}

		for (int i = 10; i < 15; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				categorySports,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

		List<InventoryEntity> inventories = new ArrayList<>();
		for (ProductEntity product : products) {
			InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);

		// when
		PageRequest pageRequest = PageRequest.of(0, 5);
		Page<ProductResponse.Search> search = adminProductService.search(
			AccountRole.ADMIN,
			"운동",
			ProductSearchType.CATEGORY,
			pageRequest
		);

		// then
		assertThat(search.getTotalElements()).isEqualTo(5);
		assertThat(search.getTotalPages()).isEqualTo(1);
		assertThat(search.getContent().size()).isEqualTo(5);
	}

	@Test
	void 상품_목록_조회__검색_상품명() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		List<ProductEntity> products = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				categorySports,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

		List<InventoryEntity> inventories = new ArrayList<>();
		for (ProductEntity product : products) {
			InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);

		// when
		PageRequest pageRequest = PageRequest.of(0, 5);
		Page<ProductResponse.Search> search = adminProductService.search(
			AccountRole.ADMIN,
			"5",
			ProductSearchType.NAME,
			pageRequest
		);

		// then
		assertThat(search.getTotalElements()).isEqualTo(1);
		assertThat(search.getTotalPages()).isEqualTo(1);
		assertThat(search.getContent().size()).isEqualTo(1);
	}

	@Test
	void 상품_상세_조회() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		ProductEntity product = ProductEntity.create("상품", 1000L, categorySports, testSeller);
		productRepository.save(product);
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
		inventoryRepository.save(inventory);

		// when
		ProductResponse.Detail detail = adminProductService.detail(AccountRole.ADMIN, product.getId());

		// then
		assertThat(detail.name()).isEqualTo("상품");
		assertThat(detail.categoryId()).isEqualTo(categorySports.getId());
	}
}
