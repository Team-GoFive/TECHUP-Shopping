package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.ProductStatus;
import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public void create(
		String name,
		Long price,
		Long stock,
		UUID categoryId
	) {
		CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);
		ProductEntity product = ProductEntity.create(name, price, stock, category);
		productRepository.save(product);
	}

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

	@Override
	public void delete(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.delete();
	}

	@Override
	public void activate(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.activate();
	}

	@Override
	public void inActivate(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.inActivate();
	}

	@Override
	public void soldOutProducts(List<UUID> productIds) {
		productRepository.findAllById(productIds).forEach(ProductEntity::inActivate);
	}

	@Override
	public void toggleActive(UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		product.toggleActive();
	}

	@Override
	public Page<ProductResponse.Search> search(UserRole role, String keyword, ProductSearchType type, Pageable pageable) {
		return productRepository.search(role, pageable, keyword, type);
	}

	@Override
	public ProductResponse.Detail detail(UserRole role, UUID productId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);

		if (role == UserRole.MEMBER && product.getStatus() != ProductStatus.ACTIVATED) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		return ProductResponse.Detail.from(product);
	}

}
