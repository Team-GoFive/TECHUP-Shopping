package com.kt.domain.dto.response;

import java.util.List;
import java.util.UUID;

import com.kt.domain.entity.CategoryEntity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryResponse {

	public record CategoryTreeItem(
		UUID id,
		String name,
		List<CategoryTreeItem> children
	) {
		public static CategoryTreeItem of(CategoryEntity category) {
			return new CategoryTreeItem(
				category.getId(),
				category.getName(),
				category.getChildren().stream()
					.map(CategoryTreeItem::of)
					.toList()
			);
		}
	}
}
