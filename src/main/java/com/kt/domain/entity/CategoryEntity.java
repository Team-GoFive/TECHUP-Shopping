package com.kt.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "category")
@Getter
public class CategoryEntity extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private CategoryEntity parent;

	@OneToMany(mappedBy = "parent")
	private List<CategoryEntity> children = new ArrayList<>();

	protected CategoryEntity(
		String name,
		CategoryEntity parent
	) {
		this.name = name;
		this.parent = parent;

		if (parent != null) {
			parent.children.add(this);
		}
	}

	public static CategoryEntity create(
		final String name,
		final CategoryEntity parent
	) {
		return new CategoryEntity(name, parent);
	}

	public void updateName(String name) {
		this.name = name;
	}
	
}
