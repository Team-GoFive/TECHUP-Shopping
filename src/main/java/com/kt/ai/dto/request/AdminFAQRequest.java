package com.kt.ai.dto.request;

import com.kt.constant.FAQCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public class AdminFAQRequest {

	@Schema(name = "FAQCreateRequest")
	public record Create(
		@Schema(description = "FAQ 질문 제목", example = "배송이 오지 않아요.")
		String title,
		@Schema(description = "FAQ 답변 내용", example = "배송은 주문 승인 이후 2-3일 영업일 내 도착 예정입니다. 지연시 고객센터 0000-0000이나 1:1 문의 부탁드립니다.")
		String content,
		@Schema(description = "FAQ 카테고리", example = "ACCOUNT|ORDER|DELIVERY|RETURN|PAYMENT|PRODUCT|OTHER")
		FAQCategory category
	) {

	}
}
