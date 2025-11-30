package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.repository.CategoryRepository;
import com.kt.security.DefaultCurrentUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("상품 생성 - POST /api/admin/product")
class ProductCreateTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CategoryRepository categoryRepository;

	CategoryEntity testCategory;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@test.com",
		UserRole.ADMIN
	);

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_생성_실패__상품명이_공백이거나_null일_경우_400_BadRequest(
		String invalidName
	) throws Exception {
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			invalidName,
			1000L,
			100000L,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__가격이_null일_경우_400_BadRequest(
		Long price
	) throws Exception {
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			"상품명",
			price,
			100000L,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__재고가_null일_경우_400_BadRequest(
		Long stock
	) throws Exception {
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			"상품명",
			1000L,
			stock,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__카테고리_id가_null일_경우_400_BadRequest(
		UUID categoryId
	) throws Exception {
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			"상품명",
			1000L,
			1000L,
			categoryId
		);

		mockMvc.perform(post("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void 상품_생성_성공__200_OK() throws Exception {
		AdminProductRequest.Create request = new AdminProductRequest.Create(
			"상품명",
			1000L,
			100000L,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/admin/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk());
	}

}