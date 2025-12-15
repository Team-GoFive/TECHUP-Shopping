package com.kt.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.dto.response.CategoryResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class AdminCategoryServiceTest {

	@Autowired
	private AdminCategoryService adminCategoryService;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		categoryRepository.deleteAll();

		CategoryEntity parentCategory = categoryRepository.save(
			CategoryEntity.create(
				"자식있는부모",
				null
			));
		CategoryEntity parentCategory2 = categoryRepository.save(
			CategoryEntity.create(
				"자식없는부모",
				null
			));
		CategoryEntity childCategory = categoryRepository.save(
			CategoryEntity.create(
				"자식카데고리명",
				parentCategory
			));
		CategoryEntity childCategory2 = categoryRepository.save(
			CategoryEntity.create(
				"자식카테고리명2",
				parentCategory
			));
	}

	@Test
	void 카테고리_생성() {
		adminCategoryService.create("자식카테고리명", null);

		// given
		UUID id = categoryRepository.findByName("자식카데고리명")
			.map(CategoryEntity::getId)
			.orElseThrow();
		CategoryEntity foundedCategory = categoryRepository.findById(id).orElseThrow();
		// then
		assertThat(foundedCategory).isNotNull();
		assertThat(foundedCategory.getName()).isEqualTo("자식카데고리명");
	}

	@AfterEach
	void tearDown() {

		categoryRepository.deleteAll();
	}

	@Test
	void 카테고리_이름_수정() {
		// given
		CategoryEntity category = categoryRepository.save(
			CategoryEntity.create("기존이름", null)
		);

		// when
		adminCategoryService.update(category.getId(), "수정");

		// then
		CategoryEntity updated = categoryRepository.findById(category.getId()).orElse(null);

		assertThat(updated).isNotNull();
		assertThat(updated.getName()).isEqualTo("수정");
	}

	@Test
	void 카테고리_전체_조회() {
		// when
		List<CategoryResponse.CategoryTreeItem> result = adminCategoryService.getAll();
		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);

		CategoryResponse.CategoryTreeItem parent = result
			.stream()
			.filter(res -> res.name().equals("자식있는부모"))
			.findFirst()
			.orElseThrow();

		assertThat(parent).isNotNull();
		assertThat(parent.children()).hasSize(2);
	}

	@Test
	void 카테고리_삭제_자식_없음() {
		// given
		CategoryEntity category = categoryRepository.findByName("자식카테고리명2").orElse(null);

		// when
		adminCategoryService.delete(category.getId());

		// then
		assertThat(categoryRepository.findByName("자식카테고리명2")).isEmpty();
	}

	@Test
	void 카테고리_삭제_실패__자식_있음() {
		// given
		CategoryEntity category = categoryRepository.findByName("자식있는부모").orElse(null);

		assertThatThrownBy(() -> adminCategoryService.delete(category.getId()))
			.isInstanceOf(CustomException.class);
	}

}
