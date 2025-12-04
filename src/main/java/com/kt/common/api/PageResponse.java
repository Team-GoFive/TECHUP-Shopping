package com.kt.common.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageResponse<T> {
	private List<T> list;

	private long totalCount;

	private int totalPages;

	public PageResponse(Page<T> page) {
		this.list = page.getContent();
		this.totalCount = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}

}
