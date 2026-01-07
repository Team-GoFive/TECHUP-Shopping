package com.kt.controller.auth;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kt.common.MockMvcTest;
import com.kt.config.jwt.JwtTokenProvider;
import com.kt.constant.redis.RedisKey;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AuthControllerTest extends MockMvcTest {

	String TEST_EMAIL = "kimdohyun032@gmail.com";

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		accountRepository.deleteAll();
		redisCache.delete(RedisKey.SIGNUP_CODE.key(TEST_EMAIL));
	}
}