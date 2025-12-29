package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;
import com.kt.service.ReviewService;

@DisplayName("상품 리뷰 조회 - GET /api/products/{productId}/reviews")
public class ProductReviewTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	AddressRepository addressRepository;
	@Autowired
	SellerRepository sellerRepository;

	@Autowired
	OrderService orderService;

	@Autowired
	ReviewService reviewService;

	UserEntity testMember;
	CategoryEntity testCategory;
	ProductEntity testProduct;
	AddressEntity address;
	SellerEntity testSeller;

	@Autowired
	OrderProductRepository orderProductRepository;

	@BeforeEach
	void setUp() {
		testMember = UserEntityCreator.create();
		userRepository.save(testMember);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = createProduct(testCategory, testSeller);

		address = addressRepository.save(AddressCreator.createAddress(testMember));

		productRepository.save(testProduct);
		for (int i = 0; i < 3; i++) {
			List<OrderRequest.Item> items = List.of(
				new OrderRequest.Item(testProduct.getId(), 1L, testSeller.getId())
			);
			orderService.createOrder(
				testMember.getId(),
				new OrderRequest(items, address.getId())
			);
		}

		orderProductRepository.findAll()
			.forEach(orderProduct -> orderProduct.updateStatus(OrderProductStatus.PURCHASE_CONFIRMED));

		List<OrderProductEntity> list = orderProductRepository.findAll().stream().toList();
		for (int i = 0; i < 3; i++) {
			OrderProductEntity orderProduct = list.get(i);
			reviewService.create(testMember.getMobile(), orderProduct.getId(), "리뷰 내용: 리뷰" + i);
		}
	}
}
