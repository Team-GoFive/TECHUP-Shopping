package com.kt.domain.entity;

import java.util.UUID;

import com.kt.domain.entity.common.BaseEntity;
import com.kt.util.ValidationUtil;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEntity extends BaseEntity {

	private Long stock;

	private UUID productId;

	private InventoryEntity(UUID productId, Long stock) {
		this.stock = stock;
		this.productId = productId;
	}

	public static InventoryEntity create(UUID productId, Long stock) {
		ValidationUtil.validatePositive(stock, "재고수량");
		return new InventoryEntity(productId, stock);
	}

	public void addStock(Long quantity) {
		this.stock += quantity;
	}

	public void decreaseStock(Long quantity) {
		this.stock -= quantity;
	}

	public void updateStock(Long stock) {
		ValidationUtil.validatePositive(stock, "재고수량");
		this.stock = stock;
	}
}
