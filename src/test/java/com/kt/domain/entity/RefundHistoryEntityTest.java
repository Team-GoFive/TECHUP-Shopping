package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.RefundStatus;


@ActiveProfiles("test")
class RefundHistoryEntityTest {
	@Test
	void 객체생성_성공() {

		// given
		UserEntity member = UserEntity.create(
			"주문자테스터1",
			"member@test.com",
			"1234",
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
				"상세설명"
			),
			member
		);

		CategoryEntity category = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		SellerEntity seller = SellerEntity.create(
			"판매자1",
			"seller@test.com",
			"1234",
			"상점1",
			"010-1234-5678",
			"seller@test.com",
			Gender.MALE
		);

		ProductEntity product = ProductEntity.create(
			"테스트상품",
			15_000L,
			10L,
			category,
			seller
		);

		OrderProductEntity orderProduct =
			OrderProductEntity.create(
				1L,
				15_000L,
				OrderProductStatus.SHIPPING_COMPLETED,
				order,
				product
			);

		PaymentEntity payment =
			PaymentEntity.create(
				15_000L,
				3_000L,
				orderProduct
			);

		RefundHistoryEntity comparisonRefundHistory =
			RefundHistoryEntity.request(
				payment,
				orderProduct,
				18_000L,
				"단순변심"
			);

		RefundHistoryEntity subjectRefundHistory =
			RefundHistoryEntity.request(
				payment,
				orderProduct,
				18_000L,
				"단순변심"
			);

		// then
		assertThat(subjectRefundHistory).isNotNull();

		assertThat(subjectRefundHistory)
			.usingRecursiveComparison()
			.isEqualTo(comparisonRefundHistory);

		assertThat(subjectRefundHistory.getStatus())
			.isEqualTo(RefundStatus.REQUESTED);
	}

}