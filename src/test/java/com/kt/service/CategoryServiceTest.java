package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.dto.response.CategoryResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;
import com.kt.service.category.CategoryService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
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

}