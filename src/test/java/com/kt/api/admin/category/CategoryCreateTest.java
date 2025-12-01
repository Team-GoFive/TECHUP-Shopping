package com.kt.api.admin.category;

import static com.kt.common.CurrentUserCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.CategoryRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;

@DisplayName("카테고리 생성(어드민) - POST /api/admin/categories")
public class CategoryCreateTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Test
	void 카테고리_생성_성공__200_OK() throws Exception {
		// when
		CategoryRequest.Create request = new CategoryRequest.Create("휴대폰", null);

		ResultActions actions = mockMvc.perform(
			post("/api/admin/categories")
				.with(user(getAdminUserDetails()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());
		CategoryEntity saved = categoryRepository.findAll().stream().findFirst().orElseThrow();
		assertThat(saved.getName()).isEqualTo("휴대폰");
	}
}
