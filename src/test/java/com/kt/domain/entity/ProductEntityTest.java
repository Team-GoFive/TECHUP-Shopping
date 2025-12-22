package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import com.kt.constant.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.ProductStatus;

@ActiveProfiles("test")
class ProductEntityTest {

	@Test
	void 객체생성_성공(){
		CategoryEntity testCategory = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		SellerEntity testSeller = SellerEntity.create(
			"판매자1",
			"seller@test.com",
			"1234",
			"상점1",
			"010-1234-5678",
			Gender.MALE
		);

		ProductEntity comparisonProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			testCategory,
			testSeller
		);

		ProductEntity subjectProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
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
