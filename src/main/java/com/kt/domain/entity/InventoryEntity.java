package com.kt.domain.entity;

import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEntity extends BaseEntity {

	private Long stock;

	public void addStock(Long quantity) {
		this.stock += quantity;
	}

	public void decreaseStock(Long quantity) {
		this.stock -= quantity;
	}
}
