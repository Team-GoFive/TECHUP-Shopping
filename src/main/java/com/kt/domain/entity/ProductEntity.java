package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import com.kt.constant.ProductStatus;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "product")
@NoArgsConstructor(access = PROTECTED)
public class ProductEntity extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long price;

	// @Column(nullable = false)
	// private Long stock;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private CategoryEntity category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private SellerEntity seller;

	protected ProductEntity(
		String name,
		Long price,
		ProductStatus status,
		CategoryEntity category,
		SellerEntity seller
	) {
		this.name = name;
		this.price = price;
		this.status = status;
		this.category = category;
		this.seller = seller;
	}

	public static ProductEntity create(
		final String name,
		final Long price,
		final CategoryEntity category,
		final SellerEntity seller
	) {
		return new ProductEntity(
			name,
			price,
			ProductStatus.ACTIVATED,
			category,
			seller
		);
	}

	public void update(String name, Long price, CategoryEntity category) {
		this.name = name;
		this.price = price;
		this.category = category;
	}

	public void delete() {
		this.status = ProductStatus.DELETED;
	}

	public void activate() {
		this.status = ProductStatus.ACTIVATED;
	}

	public void inActivate() {
		this.status = ProductStatus.IN_ACTIVATED;
	}

	public void toggleActive() {
		if (status == ProductStatus.ACTIVATED) {
			inActivate();
			return;
		}

		if (status == ProductStatus.IN_ACTIVATED) {
			activate();
		}
	}

	public void addStock(Long quantity) {
	}

	public void decreaseStock(Long quantity) {
	}

	public boolean isSellable() {
		return status == ProductStatus.ACTIVATED;
	}
}
