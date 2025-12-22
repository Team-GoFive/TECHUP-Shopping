package com.kt.service.seller;

import static com.kt.common.SellerEntityCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.seller.SellerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.ProductStatus;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SellerProductServiceTest {

	private final SellerProductService sellerProductService;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final OrderProductRepository orderProductRepository;
	private final SellerRepository sellerRepository;
	private SellerEntity testSeller;

	@Autowired
	SellerProductServiceTest(SellerProductService sellerProductService, ProductRepository productRepository,
		CategoryRepository categoryRepository, OrderProductRepository orderProductRepository,
		SellerRepository sellerRepository) {
		this.orderProductRepository = orderProductRepository;
		this.sellerProductService = sellerProductService;
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
	void 상품_생성() {
		// given
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);
		String productName = "상품1";
		long productPrice = 1000L;
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			productName,
			productPrice,
			10L,
			category.getId()
		);

		// when
		sellerProductService.create(request.name(), request.price(), request.stock(), request.categoryId(),
			testSeller.getId());

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
		SellerProductRequest.Update request = new SellerProductRequest.Update(
			"수정된상품명",
			2000L,
			20L,
			categorySports.getId()
		);

		sellerProductService.update(
			product.getId(),
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId(),
			testSeller.getId()
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
		sellerProductService.delete(product.getId(), testSeller.getId());

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
		Page<ProductResponse.Search> search = sellerProductService.search(null, null, pageRequest, testSeller.getId());

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
		Page<ProductResponse.Search> search = sellerProductService.search(
			"운동",
			ProductSearchType.CATEGORY,
			pageRequest,
			testSeller.getId()
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
			products.add(product);
		}
		productRepository.saveAll(products);

		// when
		PageRequest pageRequest = PageRequest.of(0, 5);
		Page<ProductResponse.Search> search = sellerProductService.search(
			"5",
			ProductSearchType.NAME,
			pageRequest,
			testSeller.getId()
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
		ProductResponse.Detail detail = sellerProductService.detail(product.getId(), testSeller.getId());

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

		List<UUID> productIds = productRepository.findAll()
			.stream()
			.map(ProductEntity::getId)
			.toList();

		// when
		sellerProductService.activate(productIds, testSeller.getId());

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

		List<UUID> productIds = productRepository.findAll()
			.stream()
			.map(ProductEntity::getId)
			.toList();

		// when
		sellerProductService.inActivate(productIds, testSeller.getId());

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
		List<UUID> productIds = productRepository.findAll()
			.stream()
			.map(ProductEntity::getId)
			.toList();
		sellerProductService.inActivate(productIds, testSeller.getId());

		// when
		sellerProductService.toggleActive(product.getId(), testSeller.getId());

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

		List<UUID> productIds = productRepository.findAll()
			.stream()
			.map(ProductEntity::getId)
			.toList();
		
		sellerProductService.activate(productIds, testSeller.getId());

		// when
		sellerProductService.toggleActive(product.getId(), testSeller.getId());

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
		sellerProductService.soldOutProducts(
			products.stream().map(ProductEntity::getId).toList(), testSeller.getId()
		);

		// then
		assertThat(
			productRepository.findAll()
				.stream()
				.allMatch(it -> it.getStatus() == ProductStatus.IN_ACTIVATED)
		).isTrue();
	}
}