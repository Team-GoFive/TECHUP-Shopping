package com.kt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.CartItemEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.cart.CartItemRepository;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public CartEntity getCart(UUID userId) {
		CartEntity cart = cartRepository.findCartWithItemsOrThrow(userId);

		cart.removeUnavailableItems();

		return cart;
	}

	@Override
	public void addItem(UUID userId, UUID productId, int quantity) {
		CartEntity cart = cartRepository.findByUserIdOrThrow(userId);

		ProductEntity product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		cart.addItem(product, quantity);
	}

	@Override
	public void changeQuantity(UUID userId, UUID cartItemId, int quantity) {
		CartEntity cart = cartRepository.findByUserIdOrThrow(userId);

		CartItemEntity cartItem = cartItemRepository.findByIdOrThrow(cartItemId);

		if (!cartItem.getCart().getId().equals(cart.getId())) {
			throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
		}

		cartItem.changeQuantity(quantity);
	}

	@Override
	public void removeItem(UUID userId, UUID cartItemId) {
		CartEntity cart = cartRepository.findByUserIdOrThrow(userId);

		CartItemEntity cartItem = cartItemRepository.findByIdOrThrow(cartItemId);

		if (!cartItem.getCart().getId().equals(cart.getId())) {
			throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
		}

		cart.removeItem(cartItemId);
	}

	@Override
	public void clear(UUID userId) {
		CartEntity cart = cartRepository.findByUserIdOrThrow(userId);
		cart.getItems().clear();
	}

	@Override
	public List<CartItemEntity> getCartItemsForOrder(
		UUID userId,
		List<UUID> cartItemIds
	) {
		CartEntity cart = cartRepository.findCartWithItemsOrThrow(userId);

		List<CartItemEntity> items = cart.getItems().stream()
			.filter(item -> cartItemIds.contains(item.getId()))
			.toList();

		if (items.size() != cartItemIds.size()) {
			throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
		}

		return items;
	}

	@Override
	public void removeOrderedItems(List<CartItemEntity> orderedItems) {
		CartEntity cart = orderedItems.get(0).getCart();

		cart.getItems().removeIf(item ->
			orderedItems.stream()
				.anyMatch(ordered -> ordered.getId().equals(item.getId()))
		);
	}

}
