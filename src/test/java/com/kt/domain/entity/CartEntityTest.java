package com.kt.domain.entity;


import java.time.LocalDate;

import com.kt.common.SellerEntityCreator;
import com.kt.constant.AccountRole;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;

@ActiveProfiles("test")
class CartEntityTest {

	UserEntity testUser;
	ProductEntity testProduct;
	CategoryEntity testCategory;

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testCategory = CategoryEntity.create("테스트 카테고리", null);
		SellerEntity testSeller = SellerEntityCreator.createSeller();
		testProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			testCategory,
			testSeller
		);
	}

	@Test
	void 객체_생성_성공() {
		// when
		CartEntity cart = CartEntity.create(testUser);

		// then
		Assertions.assertNotNull(cart);
		Assertions.assertEquals(testUser, cart.getUser());
		Assertions.assertTrue(cart.getItems().isEmpty());
	}
}
