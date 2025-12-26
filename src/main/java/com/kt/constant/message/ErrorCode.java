package com.kt.constant.message;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_DOMAIN_FIELD(HttpStatus.BAD_REQUEST, "도메인 필드 오류 : {0}"),
	BODY_FIELD_ERROR(HttpStatus.BAD_REQUEST, "바디 필드 오류 : {0}"),
	ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다"),
	ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자 계정입니다"),
	AUTH_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다."),
	AUTH_REFRESH_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다. 재로그인이 필요합니다."),
	AUTH_CODE_UNAVAILABLE(HttpStatus.UNAUTHORIZED, "인증 시간이 만료되었거나, 해당 이메일로 전송된 인증 코드가 없습니다."),
	AUTH_CODE_INVALID(HttpStatus.UNAUTHORIZED, "인증 코드가 일치하지 않습니다."),
	AUTH_INVALID(HttpStatus.UNAUTHORIZED, "올바르지 않은 인증 정보입니다."),
	AUTH_INVALID_ACCESS_PATH(HttpStatus.UNAUTHORIZED, "올바르지 않은 접근 경로입니다."),
	AUTH_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	AUTH_EMAIL_UNVERIFIED(HttpStatus.UNAUTHORIZED, "인증되지 않은 이메일입니다."),
	AUTH_FAILED_LOGIN(HttpStatus.UNAUTHORIZED, "이메일 혹은 비밀번호가 일치하지 않습니다."),
	AUTH_ACCOUNT_DELETED(HttpStatus.FORBIDDEN, "해당 계정은 삭제된 계정입니다"),
	AUTH_ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "해당 계정은 비활성화된 계정입니다."),
	AUTH_ACCOUNT_RETIRED(HttpStatus.FORBIDDEN, "해당 계정은 탈퇴한 계정입니다."),
	EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."),
	PASSWORD_RESET_ALREADY_REQUESTED(HttpStatus.CONFLICT, "비밀번호 초기화 요청이 이미 접수되어 처리 중입니다."),
	PASSWORD_RESET_REQUESTS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정의 비밀번호 초기화 요청이 존재하지 않습니다."),
	PASSWORD_UPDATE_REQUESTS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정의 비밀번호 변경 요청이 존재하지 않습니다."),
	DUPLICATED_EMAIL(HttpStatus.CONFLICT, "해당 이메일로 등록된 계정이 이미 존재합니다."),
	DUPLICATED_CATEGORY(HttpStatus.CONFLICT, "중복된 카테고리명 입니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문은 존재하지 않습니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품은 존재하지 않습니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
	PARENT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 카테고리가 존재하지 않습니다."),
	ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문상품이 존재하지 않습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다."),
	CHILD_CATEGORY_EXISTS(HttpStatus.BAD_REQUEST, "자식 카테고리가 존재합니다."),
	STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "상품 재고가 없습니다."),
	PASSWORD_UNCHANGED(HttpStatus.CONFLICT, "기존 패스워드와 변경할 패스워드가 동일합니다."),
	INVALID_PASSWORD(HttpStatus.FORBIDDEN, "기존 비밀번호와 일치하지 않습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
	ORDER_ALREADY_SHIPPED(HttpStatus.BAD_REQUEST, "배송이 시작되어 취소할 수 없습니다."),
	ORDER_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "주문이 구매확정 상태이므로 취소할 수 없습니다."),
	ORDER_NOT_CONFIRMED(HttpStatus.BAD_REQUEST, "주문이 구매확정 상태가 아니므로 리뷰작성이 불가능합니다."),
	ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소가 존재하지 않습니다."),
	REVIEW_ACCESS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "리뷰에 대한 접근 권한이 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 주문상품에 대한 리뷰가 이미 존재합니다."),
	ACCOUNT_ACCESS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "계정에 대한 접근 권한이 없습니다."),
	COURIER_NOT_FOUND(HttpStatus.NOT_FOUND, "배송기사가 존재하지 않습니다."),
	ORDER_ACCESS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "주문에 대한 접근 권한이 없습니다."),
	NOT_ADMIN(HttpStatus.BAD_REQUEST, "관리자 계정이 아닙니다."),
	SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "판매자가 존재하지 않습니다."),
	PRODUCT_NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 상품이 아닙니다."),
	ADMIN_PERMISSION_REQUIRED(HttpStatus.FORBIDDEN, "관리자 계정이 아닙니다."),
	SAME_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "현재 상태와 동일한 상태로는 변경할 수 없습니다."),
	INVALID_FORCE_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "강제 변경이 허용되지 않은 상태 전이입니다."),
	INVALID_ORDER_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "현재 주문 상태에서는 판매자 확정이 불가합니다."),
	ORDER_PRODUCT_NOT_OWNER(HttpStatus.FORBIDDEN, "판매자의 주문 상품이 아닙니다."),
	ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "이미 환불이 완료되었습니다."),
	REFUND_NOT_ALLOWED(HttpStatus.FORBIDDEN, "환불이 불가능합니다."),
	REFUND_ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, "환불 진행 중입니다."),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment가 존재하지 않습니다."),
	PAY_NOT_FOUND(HttpStatus.NOT_FOUND, "Pay가 존재하지 않습니다."),
	INVALID_REFUND_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 환불 금액입니다."),
	INVALID_REFUND_REASON(HttpStatus.BAD_REQUEST, "이유는 반드시 작성해야 합니다."),
	NOT_FOUND_VECTOR_STORE(HttpStatus.NOT_FOUND, "벡터스토어가 존재하지 않습니다."),
	NOT_FOUND_FAQ(HttpStatus.NOT_FOUND, "FAQ가 존재하지 않습니다."),
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니가 존재하지 않습니다."),
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다."),
	INVALID_CART_ITEM_QUANTITY(HttpStatus.BAD_REQUEST, "장바구니 상품 수량은 1 이상이어야 합니다."),
	MAX_CART_ITEM_QUANTITY(HttpStatus.FORBIDDEN, "장바구니에 담을 수 있는 최대 수량은 100개입니다."),

	;
	private final HttpStatus status;
	private final String message;

	public String format(Object... args) {
		return MessageFormat.format(this.message, args);
	}

}
