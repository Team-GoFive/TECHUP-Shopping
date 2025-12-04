package com.kt.domain.entity;

import com.kt.constant.Gender;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.UserRole;

import com.kt.exception.FieldValidationException;
import com.kt.util.EncryptUtil;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
public class PasswordRequestEntityTest {

	UserEntity user;

	@BeforeEach
	void setup() {
		user = UserEntity.create(
			"테스트유저",
			"test@email.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);

		String testKey = "techup-shopping-encrypt-test-key";
		EncryptUtil.loadKey(testKey);
	}

	@Test
	void 객체_생성_성공_변경_요청() {
		PasswordRequestEntity passwordRequest = PasswordRequestEntity.create(
			user,
			"1234",
			PasswordRequestType.UPDATE
		);

		assertNotNull(passwordRequest);
		log.info("encrypted password : {}", passwordRequest.getEncryptedPassword());
		log.info("decrypt password : {}", EncryptUtil.decrypt(passwordRequest.getEncryptedPassword()));
	}

	@Test
	void 객체_생성_성공_초기화_요청() {
		PasswordRequestEntity passwordRequest = PasswordRequestEntity.create(
			user,
			"1234",
			PasswordRequestType.RESET
		);

		assertNotNull(passwordRequest);
		assertNull(passwordRequest.getEncryptedPassword());
		log.info("getEncryptedPassword :: {}", passwordRequest.getEncryptedPassword());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " " })
	void 객체_생성_실패_변경요청시_패스워드_null_공백_빈문자열(String requestedPassword) {

		FieldValidationException exception = assertThrowsExactly(
			FieldValidationException.class,
			() -> PasswordRequestEntity.create(
				user,
				requestedPassword,
				PasswordRequestType.UPDATE
			)
		);

		log.info("exception getMessage :: {}", exception.getMessage());
		log.info("exception get errorMessage :: {}", exception.getErrorMessage());
		assertEquals("INVALID_DOMAIN_FIELD", exception.getMessage());

	}
}
