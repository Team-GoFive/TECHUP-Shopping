package com.kt.api.category;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;

@DisplayName("카테고리 전체 조회 - GET /api/categories")
public class CategoryListTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	CategoryEntity testCategory1;
	CategoryEntity testCategory2;

	@BeforeEach
	void setUp() throws Exception {
		testCategory1 = createCategory();
		testCategory2 = createCategory();
		categoryRepository.save(testCategory1);
		categoryRepository.save(testCategory2);
	}

	@Test
	void 카테고리_전체_조회__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/categories")
				.with(user(getMemberUserDetails()))
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(2));
	}
}
