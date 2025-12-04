package com.kt.constant.searchtype;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductSearchType {
	CATEGORY("카테고리"),
	NAME("상품명");
	private final String description;
}
