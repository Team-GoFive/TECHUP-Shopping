package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.UserRole;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface ProductService {

	void create(String name, Long price, Long stock, UUID categoryId);

	void update(UUID productId, String name, Long price, Long stock, UUID categoryId);

	void delete(UUID productId);

	void activate(UUID productId);

	void inActivate(UUID productId);

	void soldOutProducts(List<UUID> productIds);

	void toggleActive(UUID productId);

	Page<ProductResponse.Search> search(UserRole role, String keyword, ProductSearchType type, Pageable pageable);

	ProductResponse.Detail detail(UserRole role, UUID productId);
}
