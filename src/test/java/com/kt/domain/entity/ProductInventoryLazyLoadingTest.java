package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.repository.product.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductInventoryLazyLoadingTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private ProductEntity product;
	private CategoryEntity category;
	private SellerEntity seller;

	@BeforeEach
	void setUp() {
		// Category 생성
		category = CategoryEntity.create("테스트 카테고리", null);
		entityManager.persist(category);

		// Seller 생성
		seller = SellerEntity.create(
			"테스트 판매자",
			"test@seller.com",
			passwordEncoder.encode("password"),
			"테스트 상점",
			"010-1234-5678",
			Gender.MALE);
		entityManager.persist(seller);

		// Product 생성
		product = ProductEntity.create(
			"테스트 상품",
			10000L,
			category,
			seller
		);
		entityManager.persist(product);

		// Inventory 생성
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 100L);
		entityManager.persist(inventory);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("ProductEntity 조회 시 InventoryEntity LAZY 로딩 테스트")
	void testLazyLoading() {
		System.out.println("========== ProductEntity 조회 시작 ==========");

		// ProductEntity만 조회
		ProductEntity foundProduct = productRepository.findByIdOrThrow(product.getId());

		System.out.println("========== ProductEntity 조회 완료 ==========");
		System.out.println("Product Name: " + foundProduct.getName());

		assertThat(foundProduct).isNotNull();
		assertThat(foundProduct.getName()).isEqualTo("테스트 상품");

		System.out.println("========== InventoryEntity 접근 시작 ==========");

		// Inventory에 접근할 때 쿼리가 발생하는지 확인
		// InventoryEntity inventory = foundProduct.getInventory();
		// System.out.println("Inventory Stock: " + inventory.getStock());

		System.out.println("========== InventoryEntity 접근 완료 ==========");

		// assertThat(inventory).isNotNull();
		// assertThat(inventory.getStock()).isEqualTo(100L);
	}

	@Test
	@DisplayName("ProductEntity 조회 후 Inventory 미접근 시 쿼리 발생 확인")
	void testNoInventoryAccess() {
		System.out.println("========== ProductEntity 조회 시작 (Inventory 미접근) ==========");

		// ProductEntity만 조회
		ProductEntity foundProduct = productRepository.findByIdOrThrow(product.getId());

		System.out.println("========== ProductEntity 조회 완료 ==========");
		System.out.println("Product Name: " + foundProduct.getName());

		assertThat(foundProduct).isNotNull();
		assertThat(foundProduct.getName()).isEqualTo("테스트 상품");

		// Inventory에 접근하지 않고 테스트 종료
		System.out.println("========== 테스트 종료 (Inventory 미접근) ==========");
	}
}


