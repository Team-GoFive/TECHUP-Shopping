package com.kt.service.seller;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@DisplayName("판매자 주문 서비스 테스트")
class SellerOrderServiceTest {

	@Autowired
	private SellerOrderService sellerOrderService;
	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;

	private SellerEntity testSeller;
	private CategoryEntity testCategory;
	private UserEntity testUser;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = CategoryEntityCreator.createCategory();
		categoryRepository.save(testCategory);
	}

	private OrderEntity createOrder() {
		ReceiverVO receiverVO = ReceiverVO.create("test", "010-1234-5678", "city", "district", "road", "detail");
		return orderRepository.save(OrderEntity.create(receiverVO, testUser));
	}

	private OrderProductEntity createOrderProduct(OrderEntity order, OrderProductStatus status) {
		ProductEntity product = ProductEntityCreator.createProduct(testCategory, testSeller);
		productRepository.save(product);
		return orderProductRepository.save(OrderProductEntity.create(1L, product.getPrice(), status, order, product));
	}

	@Test
	void 판매자_주문_상품_검색_성공() {
		// given
		OrderEntity order = createOrder();
		createOrderProduct(order, OrderProductStatus.CREATED);
		createOrderProduct(order, OrderProductStatus.SHIPPING);
		createOrderProduct(order, OrderProductStatus.SHIPPING_COMPLETED);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<com.kt.domain.dto.response.SellerOrderResponse.Search> result = sellerOrderService.searchOrderProducts(
			pageable, null, null, testSeller.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getContent()).hasSize(3);
	}

	@Test
	void 판매자_주문_상품_검색_필터링__orderProductId_성공() {
		// given
		OrderEntity order = createOrder();
		OrderProductEntity op1 = createOrderProduct(order, OrderProductStatus.CREATED);
		createOrderProduct(order, OrderProductStatus.SHIPPING);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<com.kt.domain.dto.response.SellerOrderResponse.Search> result = sellerOrderService.searchOrderProducts(
			pageable, null, op1.getId(), testSeller.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).orderProductId()).isEqualTo(op1.getId());
	}

	@Test
	void 판매자_주문_상품_검색_필터링__status_성공() {
		// given
		OrderEntity order = createOrder();
		createOrderProduct(order, OrderProductStatus.CREATED);
		createOrderProduct(order, OrderProductStatus.SHIPPING);
		createOrderProduct(order, OrderProductStatus.SHIPPING);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<com.kt.domain.dto.response.SellerOrderResponse.Search> result = sellerOrderService.searchOrderProducts(
			pageable, OrderProductStatus.SHIPPING, null, testSeller.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).status()).isEqualTo(OrderProductStatus.SHIPPING);
	}

	@Test
	void 판매자_결제_상품_컨펌_성공() {
		// given
		OrderEntity order = createOrder();
		OrderProductEntity op1 = createOrderProduct(order, OrderProductStatus.PENDING_APPROVE);

		// when
		sellerOrderService.confirmPaidOrderProduct(op1.getId(), testSeller.getId());

		// then
		assertThat(op1.getStatus()).isEqualTo(OrderProductStatus.SHIPPING_READY);

	}
}
