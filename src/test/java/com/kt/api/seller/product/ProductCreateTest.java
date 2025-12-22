package com.kt.api.seller.product;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static com.kt.common.CategoryEntityCreator.createCategory;
import static com.kt.common.SellerEntityCreator.createSeller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("판매자 상품 생성 - POST /api/seller/products")
public class ProductCreateTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	UserRepository userRepository;

	CategoryEntity testCategory;
	SellerEntity testSeller;
	UserEntity testUser;

	DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = createSeller();
		sellerRepository.save(testSeller);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_생성_실패__상품명이_공백이거나_null일_경우_400_BadRequest(String invalidName) throws Exception {
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			invalidName,
			1000L,
			100L,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/seller/products")
				.with(SecurityMockMvcRequestPostProcessors.user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__가격이_null일_경우_400_BadRequest(Long price) throws Exception {
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			"판매자 상품",
			price,
			100L,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/seller/products")
				.with(SecurityMockMvcRequestPostProcessors.user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__재고가_null일_경우_400_BadRequest(Long stock) throws Exception {
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			"판매자 상품",
			1000L,
			stock,
			testCategory.getId()
		);

		mockMvc.perform(post("/api/seller/products")
				.with(SecurityMockMvcRequestPostProcessors.user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_생성_실패__카테고리_id가_null일_경우_400_BadRequest(UUID categoryId) throws Exception {
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			"판매자 상품",
			1000L,
			100L,
			categoryId
		);

		mockMvc.perform(post("/api/seller/products")
				.with(SecurityMockMvcRequestPostProcessors.user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void 상품_생성_성공__200_OK() throws Exception {
		// given
		SellerProductRequest.Create request = new SellerProductRequest.Create(
			"판매자 상품",
			1000L,
			100L,
			testCategory.getId()
		);

		// when
		ResultActions actions = mockMvc.perform(post("/api/seller/products")
				.with(SecurityMockMvcRequestPostProcessors.user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));
	}
}
