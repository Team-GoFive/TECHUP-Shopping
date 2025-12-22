package com.kt.service.admin;

import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.ProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

import com.kt.repository.seller.SellerRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

	private final ProductRepository productRepository;

	@Override
	public void delete(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.delete();
	}

	@Override
	public Page<ProductResponse.Search> search(AccountRole role, String keyword, ProductSearchType type,
		Pageable pageable) {
		return productRepository.search(role, pageable, keyword, type);
	}

	@Override
	public ProductResponse.Detail detail(AccountRole role, UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);

		if (role == AccountRole.MEMBER && product.getStatus() != ProductStatus.ACTIVATED) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		return ProductResponse.Detail.from(product);
	}
}
