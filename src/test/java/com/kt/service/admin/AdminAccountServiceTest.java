package com.kt.service.admin;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.SendEmailTest;
import com.kt.constant.Gender;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.UserStatus;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.PasswordRequestResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AdminAccountServiceTest {

	static final String TEST_PASSWORD = "1234567891011";
	@Autowired
	AdminAccountService adminAccountService;
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
	@Autowired
	PasswordRequestRepository passwordRequestRepository;

	UserEntity testUser;
	AdminEntity testAdmin;
	CourierEntity courier1;
	CourierEntity courier2;

	@BeforeEach
	void setUp() {
		passwordRequestRepository.deleteAll();
		courierRepository.deleteAll();
		userRepository.deleteAll();
		accountRepository.deleteAll();
		adminRepository.deleteAll();

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
	void 회원_조회_성공() {
		// given

		AccountRequest.Search request = new AccountRequest.Search(
			AccountRole.MEMBER,
			null,
			null,
			"회원"
		);

		// when
		Page<?> result = adminAccountService.searchAccounts(
			request,
			Pageable.ofSize(10)
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	void 관리자_조회_성공() {
		// given

		AccountRequest.Search request = new AccountRequest.Search(
			AccountRole.ADMIN,
			null,
			null,
			""
		);

		// when
		Page<?> result = adminAccountService.searchAccounts(
			request,
			Pageable.ofSize(10)
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	void 배송기사_조회_성공() {
		// given

		AccountRequest.Search request = new AccountRequest.Search(
			AccountRole.COURIER,
			null,
			null,
			""
		);

		// when
		Page<?> result = adminAccountService.searchAccounts(
			request,
			Pageable.ofSize(10)
		);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
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

		adminAccountService.updatePassword(
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
	@SendEmailTest
	void 관리자가_계정_비밀번호_초기화_성공() {
		PasswordRequestEntity passwordRequest = PasswordRequestEntity.create(
			testUser,
			null,
			PasswordRequestType.RESET
		);
		passwordRequestRepository.save(passwordRequest);
		String originPassword = "1234";

		adminAccountService.resetAccountPassword(passwordRequest.getId());

		log.info(
			"isMatch :: {}", passwordEncoder.matches(
				originPassword, testUser.getPassword()
			)
		);

		assertFalse(
			passwordEncoder.matches(
				originPassword,
				testUser.getPassword()
			)
		);
		log.info("passwordRequest status : {}", passwordRequest.getStatus());
	}

	@Test
	@SendEmailTest
	void 관리자가_계정_비밀번호_변경_성공() {
		String originPassword = "1234";
		String updatedPassword = "1231231!";
		PasswordRequestEntity passwordRequest = PasswordRequestEntity.create(
			testUser,
			updatedPassword,
			PasswordRequestType.UPDATE
		);
		passwordRequestRepository.save(passwordRequest);

		adminAccountService.updateAccountPassword(passwordRequest.getId());

		assertFalse(
			passwordEncoder.matches(originPassword, testUser.getPassword())
		);

		log.info(
			"isMatch :: {}", passwordEncoder.matches(
				originPassword, testUser.getPassword()
			)
		);
		assertEquals(PasswordRequestStatus.COMPLETED, passwordRequest.getStatus());
		log.info("passwordRequest status : {}", passwordRequest.getStatus());
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

		adminAccountService.updatePassword(
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
				adminAccountService.updatePassword(
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
			() -> adminAccountService.updatePassword(
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

		adminAccountService.deleteAccount(courier.getId());
		AbstractAccountEntity foundedAccount = accountRepository.findByIdOrThrow(courier.getId());

		Assertions.assertEquals(UserStatus.DELETED, foundedAccount.getStatus());
	}

	@Test
	void 계정삭제_성공_hard() {
		CourierEntity courier = CourierEntity.create(
			"배송기사테스터",
			"wjd123@naver.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);
		courierRepository.save(courier);

		adminAccountService.deleteAccountPermanently(courier.getId());

		assertThatThrownBy(() -> accountRepository.findByIdOrThrow(courier.getId()))
			.isInstanceOf(CustomException.class);

	}

	@Test
	void 관리자_다른_계정_비밀번호_초기화_실패_요청사항_없음() {

		assertThatThrownBy(
			() -> adminAccountService.resetAccountPassword(testUser.getId())
		).isInstanceOf(CustomException.class);

	}

	@Test
	void 비밀번호_변경_및_초기화_요청_리스트_조회_성공() {
		String updatePassword = "123123";

		PasswordRequestEntity firstRequest = PasswordRequestEntity.create(
			testUser,
			null,
			PasswordRequestType.RESET
		);

		PasswordRequestEntity secondRequest = PasswordRequestEntity.create(
			courier1,
			updatePassword,
			PasswordRequestType.UPDATE
		);
		passwordRequestRepository.save(firstRequest);
		passwordRequestRepository.save(secondRequest);

		Pageable pageable = Pageable.ofSize(10);

		// when
		PasswordRequest.Search request = new PasswordRequest.Search(
			AccountRole.COURIER,
			null,
			null,
			""
		);

		Page<PasswordRequestResponse.Search> result =
			adminAccountService.searchPasswordRequests(request, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent()
			.stream()
			.map(PasswordRequestResponse.Search::accountId)
		).contains(courier1.getId());

	}

}