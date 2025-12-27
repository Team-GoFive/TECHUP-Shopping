package com.kt.domain.entity;

import com.kt.constant.VectorType;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "vector")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VectorStoreEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private VectorType type;

	@Column(nullable = false)
	private String storeId;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private String name;

	private VectorStoreEntity(VectorType type, String storeId, String name, String description) {
		this.type = type;
		this.storeId = storeId;
		this.name = name;
		this.description = description;
	}

	public static VectorStoreEntity create(VectorType type, String storeId, String name, String description) {
		return new VectorStoreEntity(
			type, storeId, name, description
		);
	}
}
