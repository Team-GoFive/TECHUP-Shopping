package com.kt.api.admin.category;

import static com.kt.common.CategoryEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;

@DisplayName("카테고리 삭제 (어드민) - DELETE /api/admin/categories/{categoryId}")
public class CategoryDeleteTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	CategoryEntity testCategory;

	@BeforeEach
	void setUp() throws Exception {
		testCategory = createCategory();
		categoryRepository.save(testCategory);
	}

	@Test
	void 카테고리_삭제_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			MockMvcRequestBuilders
				.delete("/api/admin/categories/{categoryId}", testCategory.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(
					CurrentUserCreator.getAdminUserDetails()))
		);

		// then
		actions.andExpect(status().isOk());
		boolean exists = categoryRepository.existsById(testCategory.getId());
		assertThat(exists).isFalse();
	}
}
