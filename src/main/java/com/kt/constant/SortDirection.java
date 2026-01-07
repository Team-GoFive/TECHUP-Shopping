package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortDirection {
	ASC("오름차순"),
	DESC("내림차순");

	private final String description;
}
