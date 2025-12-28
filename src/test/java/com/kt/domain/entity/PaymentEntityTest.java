package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import com.kt.common.SellerEntityCreator;
import com.kt.constant.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;

@ActiveProfiles("test")
class PaymentEntityTest {

	OrderProductEntity orderProductEntity;

	@BeforeEach
	void setUp() throws Exception {
		CategoryEntity testCategory = CategoryEntity.create(
			"테스트카테고리",
			null
		);

		UserEntity user = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);

		ReceiverVO receiver = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);

		OrderEntity order = OrderEntity.create(
			receiver,
			user
		);

		SellerEntity testSeller = SellerEntityCreator.createSeller();

		ProductEntity product = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			testCategory,
			testSeller
		);

		orderProductEntity = new OrderProductEntity(
			5L,
			5000L,
			OrderProductStatus.CREATED,
			order,
			product
		);
	}

	@Test
	void 객체생성_성공() {
		PaymentEntity comparisonProduct = PaymentEntity.create(
			50000L,
			3000L,
			orderProductEntity
		);

		PaymentEntity subjectProduct = PaymentEntity.create(
			50000L,
			3000L,
			orderProductEntity
		);

		assertThat(subjectProduct).isNotNull();

		assertThat(subjectProduct)
			.usingRecursiveComparison()
			.ignoringFields()
			.isEqualTo(comparisonProduct);
	}
}
