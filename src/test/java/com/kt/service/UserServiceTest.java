package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.UserEntityCreator;
import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
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

	UserEntity testUser;
	UserEntity testUser2;
	UserEntity testAdmin;
	OrderEntity testOrder;
	ProductEntity testProduct;
	OrderProductEntity testOrderProduct;
	UUID userId;
	UUID AdminId;

	@AfterEach
	void clearUp() {
		reviewRepository.deleteAll();
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testUser2 = UserEntity.create(
			"주문자테스터2",
			"dohyun@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testAdmin = UserEntity.create(
			"어드민테스터",
			"dohyun@naver.com",
			"1234",
			UserRole.ADMIN,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		userRepository.save(testUser);
		UserEntity savedUser = userRepository.save(testUser2);
		UserEntity savedAdmin = userRepository.save(testAdmin);
		userId = savedUser.getId();
		AdminId = savedAdmin.getId();

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

		testProduct = ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			category
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
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"0101010"
		);

		UserEntity savedUser = userRepository.save(user);

		userId = savedUser.getId();

		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);

		ProductEntity product = ProductEntity.create(
			"테스트물건",
			3L,
			3L,
			category
		);

		ProductEntity savedProduct = productRepository.save(product);

		OrderEntity order = OrderEntity.create(
			ReceiverVO.create("이름", "번호", "도시", "시군구", "동", "상세"),
			savedUser
		);
		orderRepository.save(order);
		// when
		UserResponse.Orders foundOrder = userService.getOrdersByUserId(userId, userId);

		// then
		assertThat(foundOrder).isNotNull();
		assertThat(foundOrder.userId()).isEqualTo(userId);
		assertThat(foundOrder.orders()).isNotEmpty();
	}

	@Test
	void 리뷰_가능한_주문상품_존재() {
		testOrderProduct.updateStatus(OrderProductStatus.PURCHASE_CONFIRMED);
		orderRepository.save(testOrder);

		PageRequest pageRequest = PageRequest.of(0, 10);
		OrderProductResponse.SearchReviewable savedOrderProductResponse = userService
			.getReviewableOrderProducts(pageRequest, testUser.getId())
			.stream()
			.findFirst()
			.orElse(null);

		Assertions.assertNotNull(savedOrderProductResponse);
		Assertions.assertEquals(testOrderProduct.getId(), savedOrderProductResponse.orderProductId());
	}

	@Test
	void 리뷰_가능한_주문상품_없음__작성한_리뷰_존재() {
		testOrderProduct.updateStatus(OrderProductStatus.PURCHASE_CONFIRMED);

		ReviewEntity review = ReviewEntity.create("테스트리뷰내용");
		review.mapToOrderProduct(testOrderProduct);
		reviewRepository.saveAndFlush(review);

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<OrderProductResponse.SearchReviewable> savedOrderProductResponses = userService
			.getReviewableOrderProducts(pageRequest, testUser.getId());

		Assertions.assertEquals(0, savedOrderProductResponses.getContent().size());
	}

	@Test
	void 리뷰_가능한_주문상품_없음__주문_리뷰가능_상태_아님() {
		testOrderProduct.updateStatus(OrderProductStatus.CANCELED);
		orderRepository.save(testOrder);

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<OrderProductResponse.SearchReviewable> savedOrderProductResponses = userService
			.getReviewableOrderProducts(pageRequest, testUser.getId());

		Assertions.assertEquals(0, savedOrderProductResponses.getContent().size());
	}

	@Test
	void 유저_리스트_조회() {

		// when
		Page<UserResponse.Search> result = userService.getUsers(testAdmin.getId(), Pageable.ofSize(10), "테스터",
			UserRole.MEMBER);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	void 어드민_리스트_조회() {

		// when
		Page<UserResponse.Search> result = userService.getUsers(testAdmin.getId(), Pageable.ofSize(10), "어드민",
			UserRole.ADMIN);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	void 유저_상세_본인조회() {
		UserResponse.UserDetail savedUser = userService.getUserDetail(userId, userId);

		// then
		assertThat(userId).isNotNull();
		assertThat(savedUser.name()).isEqualTo("주문자테스터2");
	}

	@Test
	void 어드민_상세_본인조회() {
		UserResponse.UserDetail savedUser = userService.getAdminDetail(AdminId, AdminId);

		// then
		assertThat(AdminId).isNotNull();
		assertThat(savedUser.name()).isEqualTo("어드민테스터");
	}

	@Test
	void 유저_상세_조회__실패_다른사람조회() {
		assertThatThrownBy(
			() -> userService.getUserDetail(userId, AdminId)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}

	@Test
	void 어드민_상세_다른어드민조회__성공() {
		UserEntity someAdmin = UserEntityCreator.createAdmin();
		userRepository.save(someAdmin);

		// when
		UserResponse.UserDetail savedUser = userService.getAdminDetail(someAdmin.getId(), AdminId);

		// then
		assertThat(AdminId).isNotNull();
		assertThat(savedUser.name()).isEqualTo("어드민테스터");
	}

	@Test
	void 유저_상태_변경_disabled() {
		// when
		userService.disableUser(testAdmin.getId(), testUser.getId());
		UserEntity foundedUser = userRepository.findById(testUser.getId()).orElseThrow();

		// then
		assertThat(foundedUser).isNotNull();
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.DISABLED);
	}

	@Test
	void 유저_상태_변경__실패_어드민아님() {
		// then
		assertThatThrownBy(
			() -> userService.disableUser(testUser2.getId(), testUser.getId())
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}

	@Test
	void 유저_상태_변경_enabled() {

		// when
		userService.disableUser(testAdmin.getId(), testUser.getId());
		userService.enableUser(testAdmin.getId(), testUser.getId());
		UserEntity foundedUser = userRepository.findById(testUser.getId()).orElseThrow();

		// then
		assertThat(foundedUser).isNotNull();
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.ENABLED);

	}

	@Test
	void 유저_상태_변경_retired() {

		// when
		userService.retireUser(testAdmin.getId(), testUser.getId());
		UserEntity foundedUser = userRepository.findById(testUser.getId()).orElseThrow();
		// then
		assertThat(foundedUser).isNotNull();
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.RETIRED);

	}

	@Test
	void 유저_상태_변경_delete() {
		// when
		userService.deleteUser(testUser.getId(), testUser.getId());
		UserEntity foundedUser = userRepository.findById(testUser.getId()).orElseThrow();
		// then
		assertThat(foundedUser).isNotNull();
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.DELETED);
	}

	@Test
	void 어드민_유저_생성() {
		// given
		SignupRequest.SignupMember request = new SignupRequest.SignupMember(
			"어드민생성",
			"admin@test.com",
			"1234",
			Gender.MALE,
			LocalDate.of(1995, 5, 5),
			"010-5555-5555"
		);

		// when
		userService.createAdmin(testAdmin.getId(), request);

		// then
		UserEntity admin = userRepository.findByEmail("admin@test.com")
			.orElse(null);

		assertThat(admin.getName()).isEqualTo("어드민생성");
		assertThat(admin.getEmail()).isEqualTo("admin@test.com");
		assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
		assertThat(admin.getPassword()).isNotEqualTo("1234");
	}

	@Test
	void 어드민_유저_생성__실패_어드민아님() {
		// given
		SignupRequest.SignupMember request = new SignupRequest.SignupMember(
			"어드민생성",
			"admin@test.com",
			"1234",
			Gender.MALE,
			LocalDate.of(1995, 5, 5),
			"010-5555-5555"
		);

		// then
		assertThatThrownBy(
			() -> userService.createAdmin(testUser.getId(), request)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ADMIN.name());
	}

	@Test
	void 어드민_삭제_성공() {
		userService.deleteAdmin(testAdmin.getId(), testAdmin.getId());
		Assertions.assertEquals(UserStatus.DELETED, testAdmin.getStatus());
	}

	@Test
	void 어드민_삭제__실패_일반계정() {
		assertThatThrownBy(
			() -> userService.deleteAdmin(testUser.getId(), testAdmin.getId())
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}

	@Test
	void 어드민_삭제__실패_대상이_어드민아님() {
		assertThatThrownBy(
			() -> userService.deleteAdmin(testAdmin.getId(), testUser.getId())
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ADMIN.name());
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	void 유저_하드_딜리트_성공() {
		// given
		UserEntity user = UserEntity.create(
			"삭제",
			"aaa",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1111, 1, 1),
			"111"
		);
		UserEntity savedUser = userRepository.save(user);

		OrderEntity order = OrderEntity.create(
			ReceiverVO.create("이름", "번호", "도시", "시군구", "동", "상세"),
			savedUser
		);
		OrderEntity savedOrder = orderRepository.save(order);

		// when
		userService.deleteUserPermanently(testAdmin.getId(), savedUser.getId());

		// then
		assertThat(userRepository.existsById(savedUser.getId())).isFalse();
		OrderEntity foundOrder = orderRepository.findById(savedOrder.getId()).orElse(null);
		assertThat(foundOrder).isNotNull();
	}

	@Test
	void 내정보조회_성공() {
		UserResponse.UserDetail foundedDetail = userService.getUserDetailSelf(testUser.getId());

		Assertions.assertNotNull(foundedDetail);
		Assertions.assertEquals(testUser.getName(), foundedDetail.name());
		Assertions.assertEquals(testUser.getEmail(), foundedDetail.email());
	}

	@Test
	void 내정보수정_성공() {
		UserRequest.UpdateDetails updateDetails = new UserRequest.UpdateDetails(
			"변경된테스터",
			"010-5678-1234",
			LocalDate.of(1955, 3, 5),
			Gender.FEMALE
		);

		userService.updateUserDetailSelf(
			testUser.getId(),
			updateDetails
		);

		Assertions.assertEquals(testUser.getName(), updateDetails.name());
		Assertions.assertEquals(testUser.getMobile(), updateDetails.mobile());
	}
}