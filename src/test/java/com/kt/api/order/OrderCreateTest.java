package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주문 생성 - POST /api/orders")
public class OrderCreateTest extends MockMvcTest {
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	AccountRepository accountRepository;

	ArrayList<ProductEntity> products = new ArrayList<>();

	UserEntity testMember;
	AddressEntity address;
	ProductEntity testProduct1;
	ProductEntity testProduct2;
	SellerEntity testSeller;
	@Autowired
	private AddressRepository addressRepository;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		accountRepository.save(testSeller);

		testProduct1 = createProduct(category, testSeller);
		productRepository.save(testProduct1);

		testProduct2 = createProduct(category, testSeller);
		productRepository.save(testProduct2);

		products.add(testProduct1);
		products.add(testProduct2);

		address = addressRepository.save(AddressCreator.createAddress(testMember));
	}

	@Test
	void 주문_생성_성공__200_OK() throws Exception {
		// when
		OrderRequest request = new OrderRequest(
			List.of(new OrderRequest.Item(testProduct1.getId(), 1L, testSeller.getId())), address.getId()
		);

		ResultActions actions = mockMvc.perform(
			post("/api/orders")
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(testMember.getEmail())))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());
		assertThat(orderRepository.count()).isEqualTo(1L);
	}

	@Test
	void 여러_상품에_대하여_주문_생성_성공__200_OK() throws Exception {
		// when
		OrderRequest request = new OrderRequest(
			List.of(
				new OrderRequest.Item(testProduct1.getId(), 1L, testSeller.getId()),
				new OrderRequest.Item(testProduct2.getId(), 2L, testSeller.getId())),
			address.getId()
		);

		ResultActions actions = mockMvc.perform(
			post("/api/orders")
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(testMember.getEmail())))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());
		assertThat(orderRepository.count()).isEqualTo(1L);
	}

	// TODO: 재고 수량 부족으로 인한 주문 생성 실패 테스트 추가하기 (에러 핸들러 추가후)
}
