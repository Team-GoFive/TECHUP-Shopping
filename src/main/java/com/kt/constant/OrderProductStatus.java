package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderProductStatus {
	CREATED("주문생성"),
	WAITING_PAYMENT("결제대기"), // TODO: 삭제 필요
	PAID("결제완료"),
	PURCHASE_CONFIRMED("구매확정"),
	CANCELED("주문취소"),
	SHIPPING_READY("배송대기"),
	SHIPPING("배송중"),
	SHIPPING_COMPLETED("배송완료"),
	RETURN_WAITING("반품대기"),
	RETURN_SHIPPING("반품배송중"),
	RETURN_ARRIVAL("반품도착"),
	RETURN_CONFIRMED("반품확정");

	private final String description;
}
