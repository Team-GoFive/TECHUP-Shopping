package com.kt.domain.entity;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CategoryEntityTest {

	@Test
	void 객체생성_성공(){
		CategoryEntity parentCategory = CategoryEntity.create(
			"부모카테고리명",
			null
		);
		CategoryEntity childCategory = CategoryEntity.create(
				"자식카데고리명",
				parentCategory
		);

		Assertions.assertNotNull(childCategory);

		Assertions.assertEquals(parentCategory, childCategory.getParent());
		Assertions.assertEquals("자식카데고리명", childCategory.getName());
	}
}