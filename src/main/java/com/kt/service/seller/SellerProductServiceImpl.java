package com.kt.service.seller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.util.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProductServiceImpl implements SellerProductService {
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final SellerRepository sellerRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public void create(String name, Long price, Long stock, UUID categoryId, UUID sellerId) {
		CategoryEntity category = categoryRepository.findByIdOrThrow(categoryId);
		SellerEntity seller = sellerRepository.findByIdOrThrow(sellerId);

		ProductEntity product = ProductEntity.create(name, price, category, seller);
		productRepository.save(product);
		InventoryEntity inventory = InventoryEntity.create(product.getId(), stock);
		inventoryRepository.save(inventory);
	}

	@Override
	public void update(UUID productId, String name, Long price, Long stock, UUID categoryId, UUID sellerId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		InventoryEntity inventory = inventoryRepository.findByProductIdOrThrow(productId);
		inventory.updateStock(stock);
		Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		CategoryEntity category = categoryRepository.findByIdOrThrow(categoryId);
		product.update(name, price, category);
	}

	@Override
	public void delete(UUID productId, UUID sellerId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		product.delete();
	}

	@Override
	public void activate(List<UUID> productIds, UUID sellerId) {
		productIds.forEach(productId -> {
			ProductEntity product = productRepository.findByIdOrThrow(productId);
			Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
			product.activate();
		});
	}

	public void inActivate(List<UUID> productIds, UUID sellerId) {
		productIds.forEach(productId -> {
			ProductEntity product = productRepository.findByIdOrThrow(productId);
			Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
			product.inActivate();
		});
	}

	@Override
	public void toggleActive(UUID productId, UUID sellerId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		product.toggleActive();
	}

	@Override
	public void soldOutProducts(List<UUID> productIds, UUID sellerId) {
		for (UUID productId : productIds) {
			ProductEntity product = productRepository.findByIdOrThrow(productId);
			Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);

			InventoryEntity inventory = inventoryRepository.findByProductIdOrThrow(productId);
			inventory.decreaseStock(inventory.getStock());
		}
	}

	@Override
	public Page<ProductResponse.Search> search(
		String keyword,
		ProductSearchType type,
		Pageable pageable,
		UUID sellerId) {
		return productRepository.searchForSeller(pageable, keyword, type, sellerId);
	}

	@Override
	public ProductResponse.Detail detail(UUID productId, UUID sellerId) {
		ProductEntity product = productRepository.findByIdOrThrow(productId);
		Preconditions.validate(product.getSeller().getId() == sellerId, ErrorCode.ORDER_PRODUCT_NOT_OWNER);
		InventoryEntity inventory = inventoryRepository.findByProductIdOrThrow(productId);
		return ProductResponse.Detail.from(product, inventory);
	}

}
