package com.kt.service.admin;

import java.util.List;
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
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

import com.kt.domain.entity.SellerEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.seller.SellerRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final SellerRepository sellerRepository;

	// TODO: seller로 이전
	@Override
	public void create(
		String name,
		Long price,
		Long stock,
		UUID categoryId,
		UUID sellerId
	) {
		CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);
		SellerEntity seller = sellerRepository.findByIdOrThrow(sellerId);
		ProductEntity product = ProductEntity.create(name, price, stock, category, seller);
		productRepository.save(product);
	}

	// TODO: seller로 이전
	@Override
	public void update(
		UUID productId,
		String name,
		Long price,
		Long stock,
		UUID categoryId
	) {
		CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.update(name, price, stock, category);
	}

	// TODO: seller와 공존
	@Override
	public void delete(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.delete();
	}

	// TODO: seller와 공존
	@Override
	public void activate(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.activate();
	}

	// TODO: seller와 공존
	@Override
	public void inActivate(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.inActivate();
	}

	@Override
	public void soldOutProducts(List<UUID> productIds) {
		productRepository.findAllById(productIds).forEach(ProductEntity::inActivate);
	}

	// TODO: seller와 공존
	@Override
	public void toggleActive(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.toggleActive();
	}

	// TODO: seller와 공존
	@Override
	public Page<ProductResponse.Search> search(AccountRole role, String keyword, ProductSearchType type, Pageable pageable) {
		return productRepository.search(role, pageable, keyword, type);
	}

	// TODO: seller와 공존
	@Override
	public ProductResponse.Detail detail(AccountRole role, UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);

		if (role == AccountRole.MEMBER && product.getStatus() != ProductStatus.ACTIVATED) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		return ProductResponse.Detail.from(product);
	}
}
