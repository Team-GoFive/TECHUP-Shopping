package com.kt.repository.product;

import com.kt.constant.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface ProductRepositoryCustom {
	Page<ProductResponse.Search> search(AccountRole role, Pageable pageable, String keyword, ProductSearchType type);
}
