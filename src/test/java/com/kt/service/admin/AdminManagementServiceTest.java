package com.kt.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import com.kt.common.AdminCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.AdminEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;

import com.kt.repository.admin.AdminRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import com.kt.constant.AccountRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.UserStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AdminManagementServiceTest {

	@Autowired
	AdminUserService adminUserService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	AccountRepository accountRepository;

	UserEntity testUser;
	UserEntity testUser2;
	AdminEntity testAdmin;
	OrderEntity testOrder;
	ProductEntity testProduct;
	OrderProductEntity testOrderProduct;
	SellerEntity testSeller;

	@AfterEach
	void clearUp() {
		reviewRepository.deleteAll();
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		accountRepository.deleteAll();
		adminRepository.deleteAll();

	}

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntityCreator.create();

		testUser2 = UserEntity.create(
			"주문자테스터2",
			"dohyun@naver.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testAdmin = AdminCreator.create();

		userRepository.save(testUser);
		userRepository.save(testUser2);
		adminRepository.save(testAdmin);

		ReceiverVO receiver = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);

		testOrder = OrderEntity.create(
			receiver,
			testUser
		);
		orderRepository.save(testOrder);

		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		accountRepository.save(testSeller);

		testProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			category,
			testSeller
		);
		productRepository.save(testProduct);

		testOrderProduct = new OrderProductEntity(
			5L,
			5000L,
			OrderProductStatus.CREATED,
			testOrder,
			testProduct
		);
		orderProductRepository.save(testOrderProduct);
	}

	@Test
	void 내_주문_조회() {
		UserEntity user = UserEntity.create(
			"김도현",
			"ddd",
			"111",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"0101010"
		);

		userRepository.save(user);

		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		ProductEntity product = ProductEntity.create(
			"테스트물건",
			3L,
			3L,
			category,
			testSeller
		);

		productRepository.save(product);

		OrderEntity order = OrderEntity.create(
			ReceiverVO.create("이름", "번호", "도시", "시군구", "동", "상세"),
			user
		);
		orderRepository.save(order);
		// when
		UserResponse.Orders foundOrder = adminUserService.getOrdersByUserId(testUser.getId());

		// then
		assertThat(foundOrder).isNotNull();
		assertThat(foundOrder.userId()).isEqualTo(testUser.getId());
		assertThat(foundOrder.orders()).isNotEmpty();
	}

	@Test
	void 유저_리스트_조회() {

		// when
		Page<UserResponse.Search> result = adminUserService.getUsers(Pageable.ofSize(10), "테스터",
			AccountRole.MEMBER);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	void 유저_상세_본인조회() {
		UserResponse.UserDetail savedUser = adminUserService.getUserDetail(testUser.getId());

		// then
		assertNotNull(savedUser.id());
		assertThat(savedUser.name()).isEqualTo("테스트유저");
	}

	@Test
	void 유저_상세_조회__실패_다른사람조회() {
		assertThatThrownBy(
			() -> adminUserService.getUserDetail(testUser.getId())
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}

	@Test
	void 유저_상태_변경_disabled() {
		// when
		adminUserService.disableUser(testUser.getId());

		// then
		assertEquals(UserStatus.DISABLED, testUser.getStatus());
	}

	@Test
	void 유저_상태_변경_enabled() {
		// when
		testUser.disabled();
		log.info("testUser status :: {}", testUser.getStatus());
		adminUserService.enableUser(testUser.getId());

		// then
		assertEquals(UserStatus.ENABLED, testUser.getStatus());
	}

	@Test
	void 유저_상태_변경_retired() {
		// when
		adminUserService.retireUser(testUser.getId());

		// then
		assertEquals(UserStatus.RETIRED, testUser.getStatus());
	}

	@Test
	void 유저_상태_변경_delete() {
		// when
		adminUserService.deleteUser(testUser.getId());

		// then
		assertEquals(UserStatus.DELETED, testUser.getStatus());
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	void 유저_하드_딜리트_성공() {
		// given
		UserEntity user = UserEntity.create(
			"삭제",
			"aaa",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1111, 1, 1),
			"111"
		);

		userRepository.save(user);

		// when
		adminUserService.deleteUserPermanently(user.getId());

		// then
		UserEntity deletedUser = userRepository.findById(user.getId()).orElse(null);
		assertNull(deletedUser);
	}

}
