package com.kt.repository.product;

import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface ProductRepositoryCustom {
	Page<ProductResponse.Search> search(AccountRole role, Pageable pageable, String keyword, ProductSearchType type);

	Page<ProductResponse.Search> searchForSeller(Pageable pageable, String keyword, ProductSearchType type,
		UUID sellerId);
}
