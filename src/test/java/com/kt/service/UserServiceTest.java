package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.AdminEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.constant.AccountRole;

import com.kt.repository.admin.AdminRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.Gender;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.UserStatus;
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
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

import com.kt.repository.seller.SellerRepository;

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
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	SellerRepository sellerRepository;

	@Autowired
	AdminRepository adminRepository;

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
		sellerRepository.deleteAll();
	}

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testUser2 = UserEntity.create(
			"주문자테스터2",
			"dohyun@naver.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		testAdmin = AdminEntity.create(
			"어드민테스터",
			"dohyun@naver.com",
			"1234",
			Gender.MALE
		);

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
		sellerRepository.save(testSeller);

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
		ReceiverVO receiver = ReceiverVO.create(
			"이름",
			"번호",
			"도시",
			"시군구",
			"동",
			"상세"
		);
		OrderEntity order = OrderEntity.create(receiver, testUser);

		orderRepository.save(order);

		// when
		UserResponse.Orders foundOrder = userService.getOrdersByUserId(
			testUser.getId()
		);

		// then
		assertThat(foundOrder).isNotNull();
		assertThat(foundOrder.userId()).isEqualTo(testUser.getId());
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
	void 유저_상세_본인조회() {
		UserResponse.UserDetail result =
			userService.detail(testUser.getId());

		// then

		assertThat(result.name()).isEqualTo("주문자테스터1");
	}

	@Test
	void 유저_상태_변경_retired() {

		// when
		userService.retireUser(testUser.getId());

		// then
		Assertions.assertEquals(UserStatus.RETIRED, testUser.getStatus());

	}

	@Test
	void 유저_상태_변경_delete() {
		// when
		userService.deleteUser(testUser.getId());
		UserEntity foundedUser = userRepository.findById(testUser.getId()).orElseThrow();
		// then
		assertThat(foundedUser).isNotNull();
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.DELETED);
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
		UserRequest.Update updateRequest = new UserRequest.Update(
			"변경된테스터",
			"update@test.com",
			"010-5678-1234",
			LocalDate.of(1955, 3, 5)
		);

		userService.update(
			testUser.getId(),
			updateRequest
		);

		Assertions.assertEquals(testUser.getName(), updateRequest.name());
		Assertions.assertEquals(testUser.getMobile(), updateRequest.mobile());
	}
}
