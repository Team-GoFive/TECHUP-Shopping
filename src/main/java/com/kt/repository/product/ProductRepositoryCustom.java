package com.kt.repository.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.UserRole;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface ProductRepositoryCustom {
	Page<ProductResponse.Search> search(UserRole role, Pageable pageable, String keyword, ProductSearchType type);
}
