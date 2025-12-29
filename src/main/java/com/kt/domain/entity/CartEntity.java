package com.kt.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "cart")
@Table(
	name = "cart",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_cart_user_id",
			columnNames = "user_id"
		)
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity extends BaseEntity {

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserEntity user;

	@OneToMany(
		mappedBy = "cart",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	private List<CartItemEntity> items = new ArrayList<>();

	protected CartEntity(UserEntity user) {
		this.user = user;
	}

	public static CartEntity create(UserEntity user) {
		return new CartEntity(user);
	}

	public void addItem(ProductEntity product, int quantity) {
		CartItemEntity item = findItem(product);

		if (item != null) {
			item.addQuantity(quantity);
			return;
		}

		items.add(CartItemEntity.create(this, product, quantity));
	}

	public void removeItem(UUID cartItemId) {
		items.removeIf(item -> item.getId().equals(cartItemId));
	}

	public void removeUnavailableItems() {
		items.removeIf(item ->
			!item.getProduct().isSellable()
		);
	}

	private CartItemEntity findItem(ProductEntity product) {
		return items.stream()
			.filter(i -> i.getProduct().equals(product))
			.findFirst()
			.orElse(null);
	}
}
