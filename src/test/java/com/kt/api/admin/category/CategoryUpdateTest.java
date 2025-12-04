package com.kt.api.admin.category;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.CategoryRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;

@DisplayName("카테고리 수정 (어드민) - PUT /api/admin/categories/{categoryId}")
public class CategoryUpdateTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	CategoryEntity testCategory;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);
	}

	@Test
	void 카테고리_수정_성공__200_OK() throws Exception {
		// when
		CategoryRequest.Update 노트북 = new CategoryRequest.Update("노트북", null);
		ResultActions actions = mockMvc.perform(
			put("/api/admin/categories/{categoryId}", testCategory.getId())
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(노트북))
				.with(user(getAdminUserDetails()))
		);

		// then
		actions.andExpect(status().isOk());
		CategoryEntity saved = categoryRepository.findAll().stream().findFirst().orElseThrow();
		assertThat(saved.getName()).isEqualTo("노트북");
	}
}
