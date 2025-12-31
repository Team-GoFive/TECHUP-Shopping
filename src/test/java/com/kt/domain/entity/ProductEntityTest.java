package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.SellerEntityCreator;

@ActiveProfiles("test")
class ProductEntityTest {

	@Test
	void 객체생성_성공() {
		CategoryEntity testCategory = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		SellerEntity testSeller = SellerEntityCreator.createSeller();

		ProductEntity comparisonProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			testCategory,
			testSeller
		);

		ProductEntity subjectProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			testCategory,
			testSeller
		);

		assertThat(subjectProduct).isNotNull();

		assertThat(subjectProduct)
			.usingRecursiveComparison()
			.ignoringFields("detail")
			.isEqualTo(comparisonProduct);
	}
}
