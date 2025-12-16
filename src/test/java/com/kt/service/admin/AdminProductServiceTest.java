package com.kt.service.admin;

import static com.kt.common.SellerEntityCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.kt.common.SellerEntityCreator;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.ProductStatus;
import com.kt.constant.AccountRole;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.request.ProductRequest;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminProductServiceTest {

	private final AdminProductService adminProductService;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final OrderProductRepository orderProductRepository;
	private final AccountRepository accountRepository;
	private SellerEntity testSeller;

	@Autowired
	AdminProductServiceTest(AdminProductService adminProductService, ProductRepository productRepository,
		CategoryRepository categoryRepository, OrderProductRepository orderProductRepository,
		AccountRepository accountRepository) {
		this.orderProductRepository = orderProductRepository;
		this.adminProductService = adminProductService;
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.accountRepository = accountRepository;
	}

	@BeforeEach
	void setUp() {
		testSeller = createSeller();
		accountRepository.save(testSeller);
	}

	@Test
	void 상품_생성() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);
		String productName = "상품1";
		long productPrice = 1000L;
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			productName,
			productPrice,
			10L,
			category.getId(),
			testSeller.getId()
		);

		// when
		adminProductService.create(request.name(), request.price(), request.stock(), request.categoryId(),
			request.sellerId());

		// then
		ProductEntity product = productRepository.findAll()
			.stream()
			.filter(it -> it.getName().equals(productName))
			.findFirst()
			.orElseThrow();

		assertThat(product.getPrice()).isEqualTo(productPrice);
		assertThat(product.getSeller().getId()).isEqualTo(testSeller.getId());
	}

	@Test
	void 삼품_수정() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		CategoryEntity categorySports = CategoryEntity.create("책", null);
		categoryRepository.save(categorySports);

		ProductEntity product = ProductEntity.create(
			"상품1",
			1000L,
			10L,
			category,
			testSeller
		);

		productRepository.save(product);

		// when
		AdminProductRequest.Update request = new AdminProductRequest.Update(
			"수정된상품명",
			2000L,
			20L,
			categorySports.getId()
		);

		adminProductService.update(
			product.getId(),
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId()
		);

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getName()).isEqualTo(request.name());
		assertThat(savedProduct.getCategory().getId()).isEqualTo(categorySports.getId());
	}

	@Test
	void 상품_삭제() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		ProductEntity product = ProductEntity.create(
			"상품1",
			1000L,
			10L,
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
				10L,
				category,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

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
				10L,
				categoryDog,
				testSeller
			);
			products.add(product);
		}

		for (int i = 10; i < 15; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				10L,
				categorySports,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

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
				10L,
				categorySports,
				testSeller
			);
			productRepository.save(product);
		}

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
		ProductEntity product = ProductEntity.create("상품", 1000L, 10L, categorySports, testSeller);
		productRepository.save(product);

		// when
		ProductResponse.Detail detail = adminProductService.detail(AccountRole.ADMIN, product.getId());

		// then
		assertThat(detail.name()).isEqualTo("상품");
		assertThat(detail.categoryId()).isEqualTo(categorySports.getId());
	}

	@Test
	void 상품_활성화() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		ProductEntity product = ProductEntity.create("상품", 1000L, 10L, categorySports, testSeller);
		productRepository.save(product);

		// when
		adminProductService.activate(product.getId());

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}

	@Test
	void 상품_비활성화() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		ProductEntity product = ProductEntity.create("상품", 1000L, 10L, categorySports, testSeller);
		productRepository.save(product);

		// when
		adminProductService.inActivate(product.getId());

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);
	}

	@Test
	void 상품_품절_토글__비활성화에서_활성화() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		ProductEntity product = ProductEntity.create("상품", 1000L, 10L, categorySports, testSeller);
		productRepository.save(product);
		adminProductService.inActivate(product.getId());

		// when
		adminProductService.toggleActive(product.getId());

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}

	@Test
	void 상품_품절_토글__활성화에서_비활성화() {
		// given
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		ProductEntity product = ProductEntity.create("상품", 1000L, 10L, categorySports, testSeller);
		productRepository.save(product);
		adminProductService.activate(product.getId());

		// when
		adminProductService.toggleActive(product.getId());

		// then
		ProductEntity savedProduct = productRepository.findByIdOrThrow(product.getId());
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);
	}

	@Test
	void 상품_다중_품절() {
		// when
		CategoryEntity categorySports = CategoryEntity.create("운동", null);
		categoryRepository.save(categorySports);
		List<ProductEntity> products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			ProductEntity product = ProductEntity.create(
				"상품" + i,
				1000L,
				10L,
				categorySports,
				testSeller
			);
			products.add(product);
		}
		productRepository.saveAll(products);

		// when
		adminProductService.soldOutProducts(
			products.stream().map(ProductEntity::getId).toList()
		);

		// then
		assertThat(
			productRepository.findAll()
				.stream()
				.allMatch(it -> it.getStatus() == ProductStatus.IN_ACTIVATED)
		).isTrue();
	}
}
