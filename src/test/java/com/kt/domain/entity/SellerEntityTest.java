package com.kt.domain.entity;

import com.kt.constant.Gender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class SellerEntityTest {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	void 판매자_객체생성_성공() {
		SellerEntity seller = SellerEntity.create(
			"테스트판매자",
			"seller@test.com",
			passwordEncoder.encode("1234"),
			"테스트_스트어",
			"010-1234-1234",
			Gender.MALE
		);

		assertNotNull(seller);
		assertEquals(60, seller.password.length());
	}

}
