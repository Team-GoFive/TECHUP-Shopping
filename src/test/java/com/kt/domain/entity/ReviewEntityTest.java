package com.kt.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.ReviewStatus;
import com.kt.exception.FieldValidationException;

@ActiveProfiles("test")
class ReviewEntityTest {

	@Test
	void 객체생성_성공(){
		ReviewEntity review = ReviewEntity.create(
			"테스트내용"
		);
		Assertions.assertNotNull(review);
		Assertions.assertEquals("테스트내용", review.getContent());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__content_null_또는_blank(String content){
		assertThrowsExactly(
			FieldValidationException.class,
			()-> ReviewEntity.create(
				content
			)
		);
	}

}