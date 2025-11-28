package com.kt.constant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
	CREATED("주문생성"),
	WAITING_PAYMENT("결제대기"),
	PAID("결제완료"),
	PURCHASE_CONFIRMED("구매확정"),
	CANCELED("주문취소"),
	SHIPPING("배송중"),
	SHIPPING_COMPLETED("배송완료");

	private final String description;
}