package com.kt.service.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.CategoryResponse;
import com.kt.domain.entity.CategoryEntity;
import com.kt.exception.CustomException;
import com.kt.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {
	private final CategoryRepository categoryRepository;

	@Override
	public void create(String name, UUID parentId) {
		isDuplicatedCategory(name);
		CategoryEntity parentCategory = (parentId != null) ?
			categoryRepository.findById(parentId).orElseThrow(() -> new CustomException(
				ErrorCode.CATEGORY_NOT_FOUND)) : null;

		CategoryEntity category = CategoryEntity.create(name, parentCategory);
		categoryRepository.save(category);

	}

	@Override
	public void update(UUID id, String name) {
		isDuplicatedCategory(name);
		CategoryEntity category = categoryRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);

		category.updateName(name);
	}

	@Override
	public List<CategoryResponse.CategoryTreeItem> getAll() {
		List<CategoryEntity> list = categoryRepository.findAll();

		List<CategoryEntity> rootCategory = list.stream()
			.filter(category -> category.getParent() == null)
			.toList();

		return rootCategory
			.stream().map(CategoryResponse.CategoryTreeItem::of).toList();
	}

	@Override
	public void delete(UUID id) {
		CategoryEntity category = categoryRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		if (!category.getChildren().isEmpty())
			throw new CustomException(ErrorCode.CHILD_CATEGORY_EXISTS);

		categoryRepository.delete(category);
	}

	private void isDuplicatedCategory(String name) {
		if (categoryRepository.findByName(name).isPresent()) {
			throw new CustomException(ErrorCode.DUPLICATED_CATEGORY);
		}
	}
}
