package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;

public interface ProductService {

	Page<ProductResponse.Search> search(AccountRole role, String keyword, ProductSearchType type, Pageable pageable);

	ProductResponse.Detail detail(AccountRole role, UUID productId);
}
