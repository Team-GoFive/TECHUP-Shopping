package com.kt.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import com.kt.common.AdminCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;

import com.kt.domain.entity.AdminEntity;

import com.kt.repository.admin.AdminRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.account.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

	static final String TEST_PASSWORD = "1234567891011";
	@Autowired
	AccountService accountService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	CourierRepository courierRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	UserEntity testUser;
	AdminEntity testAdmin;
	CourierEntity courier1;
	CourierEntity courier2;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		testAdmin = AdminCreator.create();

		userRepository.save(testUser);
		adminRepository.save(testAdmin);

		courier1 = CourierEntity.create(
			"기사1",
			"courier1@test.com",
			"1234",
			Gender.MALE
		);

		courier2 = CourierEntity.create(
			"기사2",
			"courier2@test.com",
			"1234",
			Gender.MALE
		);

		courierRepository.save(courier1);
		courierRepository.save(courier2);
	}

	@Test
	void 회원계정_비밀번호변경_성공() {
		UserEntity user = UserEntity.create(
			"회원테스터",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);
		userRepository.save(user);

		accountService.updatePassword(
			user.getId(),
			TEST_PASSWORD,
			"12345678910"
		);

		boolean validResult = passwordEncoder.matches(
			"12345678910",
			user.getPassword()
		);

		Assertions.assertTrue(validResult);
	}

	@Test
	void 배송기사계정_비밀번호변경_성공() {
		CourierEntity courier = CourierEntity.create(
			"배송기사테스터",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);
		courierRepository.save(courier);

		accountService.updatePassword(
			courier.getId(),
			TEST_PASSWORD,
			"12345678910"
		);

		boolean validResult = passwordEncoder.matches(
			"12345678910",
			courier.getPassword()
		);

		Assertions.assertTrue(validResult);
	}

	@Test
	void 계정비밀번호변경_실패__현재_비밀번호_불일치() {
		UserEntity user = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);
		userRepository.save(user);

		assertThrowsExactly(
			CustomException.class,
			() -> {
				accountService.updatePassword(
					user.getId(),
					"틀린비밀번호입니다.......",
					"22222222222222"
				);
			}
		);
	}

	@Test
	void 계정비밀번호변경_실패__변경할_비밀번호_동일() {
		CourierEntity courier = CourierEntity.create(
			"주문자테스터2",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);
		courierRepository.save(courier);

		assertThrowsExactly(
			CustomException.class,
			() -> accountService.updatePassword(
				courier.getId(),
				TEST_PASSWORD,
				TEST_PASSWORD
			)
		);
	}

	@Test
	void 계정삭제_성공_soft() {
		CourierEntity courier = CourierEntity.create(
			"배송기사테스터",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);
		courierRepository.save(courier);

		accountService.deleteAccount(courier.getId());
		AbstractAccountEntity foundedAccount = accountRepository.findByIdOrThrow(courier.getId());

		Assertions.assertEquals(UserStatus.DELETED, foundedAccount.getStatus());
	}

}