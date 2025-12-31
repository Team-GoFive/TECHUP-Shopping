package com.kt;

import static com.kt.common.AddressCreator.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.service.OrderPaymentService;
import com.kt.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class LockTest {

	private final long productStock1 = 10L;
	private final long productStock2 = 10L;
	@Autowired
	private OrderPaymentService orderPaymentService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private OrderProductRepository orderProductRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private ProductEntity product1;
	private ProductEntity product2;

	private static UserEntity createUser(int i) {
		return UserEntity.create(
			"사용자" + i,
			"user" + i + "@email.com",
			"password",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-1234"
		);
	}

	@AfterEach
	void cleanUp() {
		// 외래키 제약조건을 고려하여 역순으로 삭제
		orderProductRepository.deleteAll();
		productRepository.deleteAll(); // 상품 삭제
		orderRepository.deleteAll();  // 주문 먼저 삭제
		addressRepository.deleteAll(); // 주소 삭제
		categoryRepository.deleteAll(); // 카테고리 삭제
		accountRepository.deleteAll();  // 계정 삭제
	}

	@BeforeEach
	void setup() {
		CategoryEntity category = CategoryEntity.create("카테고리", null);
		categoryRepository.save(category);
		SellerEntity seller = SellerEntity.create(
			"판매자",
			"seller@email.com",
			passwordEncoder.encode("password"),
			"가게명",
			"010-1234-1234",
			Gender.MALE
		);
		accountRepository.save(seller);
		product1 = ProductEntity.create(
			"상품1", 100_000L, productStock1, category, seller
		);
		product2 = ProductEntity.create(
			"상품2", 100_000L, productStock2, category, seller
		);
		productRepository.saveAll(List.of(product1, product2));
		productRepository.flush();
	}

	@Test
	void 사용자_100명이_동시에_product1_상품에_대해서_주문_시도() throws InterruptedException {
		int repeatCount = 100;
		List<UserEntity> users = new ArrayList<>();
		List<AddressEntity> addresses = new ArrayList<>();
		for (int i = 0; i < repeatCount; i++) {
			UserEntity user = createUser(i);
			AddressEntity address = createAddress(user);
			users.add(user);
			addresses.add(address);
		}
		accountRepository.saveAll(users);
		addressRepository.saveAll(addresses);
		System.out.println("============== setup ==============");
		OrderRequest.Item item = new OrderRequest.Item(
			product1.getId(),
			1L,
			UUID.randomUUID()
		);

		// 동시에 주문해야하니까 쓰레드를 100개
		ExecutorService executorService = Executors.newFixedThreadPool(repeatCount);
		CountDownLatch countDownLatch = new CountDownLatch(repeatCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		for (int i = 0; i < repeatCount; i++) {
			int finalI = i;
			executorService.submit(() -> {
				try {
					System.out.println(String.format("===== 사용자 %d 주문 시도 =====", finalI));
					OrderRequest request = new OrderRequest(
						List.of(item),
						addresses.get(finalI).getId()
					);
					orderPaymentService.orderPay(users.get(finalI).getId(), request);
					successCount.incrementAndGet();
				} catch (RuntimeException e) {
					e.printStackTrace();
					failureCount.incrementAndGet();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		ProductEntity foundProduct = productRepository.findById(product1.getId()).orElse(null);

		assertThat(successCount.get()).isEqualTo((int)productStock1);
		assertThat(failureCount.get()).isEqualTo(repeatCount - (int)productStock1);
		assertThat(foundProduct.getStock()).isEqualTo(0);
	}
}
