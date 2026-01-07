package com.kt.service.category;

import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.response.CategoryResponse;

public interface CategoryService {
	void create(String name, UUID parentId);

	void update(UUID id, String name);

	void delete(UUID id);

	List<CategoryResponse.CategoryTreeItem> getAll();
}
