package com.kt.domain.entity;


import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;

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
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testCategory = CategoryEntity.create("테스트 카테고리", null);
		testProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			testCategory
		);
	}

	@Test
	void 객체생성_성공(){
		CartEntity cart = CartEntity.create(
			5L,
			testUser,
			testProduct
		);

		Assertions.assertNotNull(cart);

		Assertions.assertEquals(testUser, cart.getUser());
		Assertions.assertEquals(testProduct, cart.getProduct());
	}
}