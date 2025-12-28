package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.SellerEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;

@ActiveProfiles("test")
class OrderProductEntityTest {
	@Test
	void 객체생성_성공() {

		// given
		UserEntity member = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"P@ssW0rd!234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		OrderEntity order = OrderEntity.create(
			ReceiverVO.create(
				"수령인",
				"010-1234-5678",
				"부산광역시",
				"상세주소",
				"101호",
				"주소설명"
			),
			member
		);

		CategoryEntity category = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		SellerEntity seller = SellerEntityCreator.createSeller();

		ProductEntity product = ProductEntity.create(
			"테스트상품",
			15_000L,
			10L,
			category,
			seller
		);

		OrderProductEntity comparisonOrderProduct =
			OrderProductEntity.create(
				2L,
				15_000L,
				OrderProductStatus.SHIPPING_COMPLETED,
				order,
				product
			);

		OrderProductEntity subjectOrderProduct =
			OrderProductEntity.create(
				2L,
				15_000L,
				OrderProductStatus.SHIPPING_COMPLETED,
				order,
				product
			);

		// then
		assertThat(subjectOrderProduct).isNotNull();

		assertThat(subjectOrderProduct)
			.usingRecursiveComparison()
			.isEqualTo(comparisonOrderProduct);
	}

}