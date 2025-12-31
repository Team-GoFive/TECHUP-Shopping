package com.kt.domain.entity;

import com.kt.domain.entity.common.BaseEntity;
import com.kt.util.ValidationUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEntity extends BaseEntity {

	private Long stock;

	@OneToOne
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity productEntity;

	protected InventoryEntity(ProductEntity productEntity, Long stock) {
		this.stock = stock;
		this.productEntity = productEntity;
	}

	public static InventoryEntity create(ProductEntity productEntity, Long stock) {
		ValidationUtil.validatePositive(stock, "재고수량");
		return new InventoryEntity(productEntity, stock);
	}

	public void addStock(Long quantity) {
		this.stock += quantity;
	}

	public void decreaseStock(Long quantity) {
		this.stock -= quantity;
	}
}
