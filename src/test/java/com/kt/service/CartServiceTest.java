package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CategoryEntityCreator;
import com.kt.common.ProductEntityCreator;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.CartItemEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.cart.CartItemRepository;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class CartServiceTest {
	@Autowired
	CartService cartService;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;

	UserEntity testUser;
	SellerEntity testSeller;
	CategoryEntity category;
	ProductEntity product1;
	ProductEntity product2;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		category = CategoryEntityCreator.createCategory();
		categoryRepository.save(category);

		product1 = productRepository.save(
			ProductEntityCreator.createProduct(category, testSeller)
		);
		product2 = productRepository.save(
			ProductEntityCreator.createProduct(category, testSeller)
		);

		cartRepository.save(CartEntity.create(testUser));
	}

	@Test
	void 장바구니_조회_성공() {
		CartEntity cart = cartService.getCart(testUser.getId());
		assertThat(cart).isNotNull();
		assertThat(cart.getItems()).isEmpty();
	}

	@Test
	void 장바구니_상품_추가_성공() {
		cartService.addItem(testUser.getId(), product1.getId(), 2);

		CartEntity cart = cartService.getCart(testUser.getId());
		assertThat(cart.getItems()).hasSize(1);

		CartItemEntity item = cart.getItems().get(0);
		assertThat(item.getProduct().getId()).isEqualTo(product1.getId());
		assertThat(item.getQuantity()).isEqualTo(2);
	}

	@Test
	void 장바구니_상품_수량_변경_성공() {
		cartService.addItem(testUser.getId(), product1.getId(), 1);

		CartItemEntity item =
			cartService.getCart(testUser.getId()).getItems().get(0);

		cartService.changeQuantity(
			testUser.getId(),
			item.getId(),
			5
		);

		CartItemEntity updated =
			cartItemRepository.findById(item.getId()).orElseThrow();

		assertThat(updated.getQuantity()).isEqualTo(5);
	}

	@Test
	void 장바구니_상품_삭제_성공() {
		cartService.addItem(testUser.getId(), product1.getId(), 1);

		CartItemEntity item =
			cartService.getCart(testUser.getId()).getItems().get(0);

		cartService.removeItem(testUser.getId(), item.getId());

		CartEntity cart = cartService.getCart(testUser.getId());
		assertThat(cart.getItems()).isEmpty();
	}

	@Test
	void 장바구니_전체_비우기_성공() {
		cartService.addItem(testUser.getId(), product1.getId(), 1);
		cartService.addItem(testUser.getId(), product2.getId(), 2);

		cartService.clear(testUser.getId());

		CartEntity cart = cartService.getCart(testUser.getId());
		assertThat(cart.getItems()).isEmpty();
	}

	@Test
	void 주문_완료_후_카트아이템_삭제_성공() {
		cartService.addItem(testUser.getId(), product1.getId(), 1);
		cartService.addItem(testUser.getId(), product2.getId(), 2);

		CartEntity cart = cartService.getCart(testUser.getId());
		List<CartItemEntity> items = cart.getItems();

		cartService.removeOrderedItems(items);

		CartEntity after = cartService.getCart(testUser.getId());
		assertThat(after.getItems()).isEmpty();
	}

	@Test
	void 장바구니_상품_중복_추가__수량_누적() {
		cartService.addItem(testUser.getId(), product1.getId(), 1);
		cartService.addItem(testUser.getId(), product1.getId(), 2);

		CartItemEntity item =
			cartService.getCart(testUser.getId()).getItems().get(0);

		assertThat(item.getQuantity()).isEqualTo(3);
	}

}