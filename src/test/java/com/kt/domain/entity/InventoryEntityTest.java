package com.kt.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.SellerEntityCreator;
import com.kt.exception.FieldValidationException;

@ActiveProfiles("test")
class InventoryEntityTest {

	private ProductEntity testProduct;

	@BeforeEach
	void setup() {
		CategoryEntity category = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		SellerEntity seller = SellerEntityCreator.createSeller();

		testProduct = ProductEntity.create(
			"테스트상품",
			15_000L,
			category,
			seller
		);
	}

	@Test
	void 객체생성_성공() {
		InventoryEntity inventory = InventoryEntity.create(
			testProduct.getId(),
			1L
		);

	}

	@Test
	void 객체생성_실패__재고값_음수() {
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				InventoryEntity.create(
					testProduct.getId(),
					-1L
				);
			}
		);
	}
}