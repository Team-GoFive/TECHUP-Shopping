package com.kt;

import static com.kt.common.AddressCreator.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.inventory.InventoryRepository;
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

	private final long productStock1 = 1000L;
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
	private InventoryRepository inventoryRepository;
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
		inventoryRepository.deleteAll(); // 재고 삭제
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
			"상품1", 100_000L, category, seller
		);
		product2 = ProductEntity.create(
			"상품2", 100_000L, category, seller
		);
		productRepository.saveAll(List.of(product1, product2));
		InventoryEntity inventory = InventoryEntity.create(product1.getId(), productStock1);
		inventoryRepository.save(inventory);
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
			1L
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

		InventoryEntity foundInventory = inventoryRepository.findByProductIdOrThrow(product1.getId());

		assertThat(successCount.get()).isEqualTo((int)productStock1);
		assertThat(failureCount.get()).isEqualTo(repeatCount - (int)productStock1);
		assertThat(foundInventory.getStock()).isEqualTo(0);
	}

	@Test
	void 사용자_100명이_동시에_product1_상품에_대해서_주문_시도하고_다른_사용자가_동시에_상품_조회()
		throws InterruptedException {
		int orderCount = 100;  // 주문하는 사용자 수
		int queryCount = 5000;   // 조회하는 사용자 수

		// 주문하는 사용자 100명 생성
		List<UserEntity> orderUsers = new ArrayList<>();
		List<AddressEntity> orderAddresses = new ArrayList<>();
		for (int i = 0; i < orderCount; i++) {
			UserEntity user = createUser(i);
			AddressEntity address = createAddress(user);
			orderUsers.add(user);
			orderAddresses.add(address);
		}
		accountRepository.saveAll(orderUsers);
		addressRepository.saveAll(orderAddresses);

		// 조회하는 사용자 10명 생성 (실제로는 조회에 사용자가 필요없지만 명확성을 위해)
		List<UserEntity> queryUsers = new ArrayList<>();
		for (int i = 0; i < queryCount; i++) {
			UserEntity user = createUser(i + orderCount);
			queryUsers.add(user);
		}
		accountRepository.saveAll(queryUsers);

		System.out.println("============== setup ==============");
		OrderRequest.Item item = new OrderRequest.Item(
			product1.getId(),
			1L,
			UUID.randomUUID()
		);

		// 주문 100개 + 조회 10개 = 총 110개의 작업을 동시에 실행
		int totalTasks = orderCount + queryCount;
		ExecutorService executorService = Executors.newFixedThreadPool(totalTasks);
		CountDownLatch countDownLatch = new CountDownLatch(totalTasks);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		// 조회 요청 시간 측정을 위한 변수들
		java.util.concurrent.atomic.AtomicLong totalQueryTime = new java.util.concurrent.atomic.AtomicLong(0);
		java.util.concurrent.atomic.AtomicLong minQueryTime = new java.util.concurrent.atomic.AtomicLong(Long.MAX_VALUE);
		java.util.concurrent.atomic.AtomicLong maxQueryTime = new java.util.concurrent.atomic.AtomicLong(0);

		// 주문 요청 100개를 동시에 submit
		for (int i = 0; i < orderCount; i++) {
			int finalI = i;
			executorService.submit(() -> {
				try {
					// System.out.println(String.format("===== 사용자 %d 주문 시도 =====", finalI));
					OrderRequest request = new OrderRequest(
						List.of(item),
						orderAddresses.get(finalI).getId()
					);
					orderPaymentService.orderPay(orderUsers.get(finalI).getId(), request);
					successCount.incrementAndGet();
				} catch (RuntimeException e) {
					e.printStackTrace();
					failureCount.incrementAndGet();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		// 상품 조회 요청 10개를 동시에 submit
		for (int j = 0; j < queryCount; j++) {
			int finalJ = j;
			executorService.submit(() -> {
				try {
					long startTime = System.currentTimeMillis();
					System.out.println(String.format("===== 사용자 %d 상품 조회 시도 =====", finalJ + orderCount));
					productRepository.findByIdOrThrow(product1.getId());
					long endTime = System.currentTimeMillis();
					long queryTime = endTime - startTime;

					// 총 시간 누적
					totalQueryTime.addAndGet(queryTime);

					// 최소 시간 갱신
					minQueryTime.updateAndGet(current -> Math.min(current, queryTime));

					// 최대 시간 갱신
					maxQueryTime.updateAndGet(current -> Math.max(current, queryTime));

					System.out.println(String.format("===== 사용자 %d 상품 조회 완료 (소요시간: %dms) =====",
						finalJ + orderCount, queryTime));
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		InventoryEntity foundInventory = inventoryRepository.findByProductIdOrThrow(product1.getId());

		// 조회 요청 시간 통계 출력
		long avgQueryTime = queryCount > 0 ? totalQueryTime.get() / queryCount : 0;
		System.out.println("\n============== 조회 요청 시간 통계 ==============");
		System.out.println(String.format("총 조회 요청 수: %d", queryCount));
		System.out.println(String.format("총 소요 시간: %dms", totalQueryTime.get()));
		System.out.println(String.format("평균 소요 시간: %dms", avgQueryTime));
		System.out.println(String.format("최소 소요 시간: %dms", minQueryTime.get() == Long.MAX_VALUE ? 0 : minQueryTime.get()));
		System.out.println(String.format("최대 소요 시간: %dms", maxQueryTime.get()));
		System.out.println("==============================================\n");

		assertThat(successCount.get()).isEqualTo((int)orderCount);
		assertThat(failureCount.get()).isEqualTo(0);
		assertThat(foundInventory.getStock()).isEqualTo(productStock1 - orderCount);
	}
}
