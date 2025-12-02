package com.kt.common;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.constraints.Min;

public record Paging(
	@Min(value = 1, message = "page 번호는 1 이상이어야 합니다.")
	int page,
	@Range(min = 1, max = 20, message = "size는 1 이상 20 이하이어야 합니다.")
	int size
) {
	public Pageable toPageable() {
		return PageRequest.of(page - 1, size);
	}
}
