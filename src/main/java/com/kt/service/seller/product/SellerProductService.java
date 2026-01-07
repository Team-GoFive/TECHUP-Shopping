package com.kt.service.seller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface SellerProductService {
	void create(String name, Long price, Long stock, UUID categoryId, UUID sellerId);

	void update(UUID productId, String name, Long price, Long stock, UUID categoryId, UUID sellerId);

	void delete(UUID productId, UUID sellerId);

	void activate(List<UUID> productIds, UUID sellerId);

	void inActivate(List<UUID> productIds, UUID sellerId);

	void toggleActive(UUID productId, UUID sellerId);

	void soldOutProducts(List<UUID> productIds, UUID sellerId);

	Page<ProductResponse.Search> search(String keyword, ProductSearchType type, Pageable pageable, UUID sellerId);

	ProductResponse.Detail detail(UUID productId, UUID sellerId);
}
