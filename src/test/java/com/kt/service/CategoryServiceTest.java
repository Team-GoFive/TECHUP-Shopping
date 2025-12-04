package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import com.kt.exception.CustomException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.domain.dto.response.CategoryResponse;
import com.kt.domain.entity.CategoryEntity;

import com.kt.repository.CategoryRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {

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
		categoryService.create("자식카테고리명", null);

		// given
		UUID id = categoryRepository.findByName("자식카데고리명")
			.map(CategoryEntity::getId)
			.orElseThrow();
		CategoryEntity foundedCategory = categoryRepository.findById(id).orElseThrow();
		// then
		assertThat(foundedCategory).isNotNull();
		assertThat(foundedCategory.getName()).isEqualTo("자식카데고리명");
	}

	@Test
	void 카테고리_이름_수정() {
		// given
		CategoryEntity category = categoryRepository.save(
			CategoryEntity.create("기존이름", null)
		);

		// when
		categoryService.update(category.getId(), "수정");

		// then
		CategoryEntity updated = categoryRepository.findById(category.getId()).orElse(null);

		assertThat(updated).isNotNull();
		assertThat(updated.getName()).isEqualTo("수정");
	}

	@Test
	void 카테고리_전체_조회() {
		// when
		List<CategoryResponse.CategoryTreeItem> result = categoryService.getAll();
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
		categoryService.delete(category.getId());

		// then
		assertThat(categoryRepository.findByName("자식카테고리명2")).isEmpty();
	}

	@Test
	void 카테고리_삭제_실패__자식_있음() {
		// given
		CategoryEntity category = categoryRepository.findByName("자식있는부모").orElse(null);

		assertThatThrownBy(() -> categoryService.delete(category.getId()))
			.isInstanceOf(CustomException.class);
	}

}