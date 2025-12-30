package com.kt.util;

import java.text.MessageFormat;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.FieldValidationException;

import org.springframework.util.StringUtils;

public class ValidationUtil {

	static final String POSITIVE_MESSAGE = "{0}은(는) 0보다 커야합니다.";
	static final String NOT_NEGATIVE_MESSAGE = "{0}은(는) 0보다 작을 수 없습니다.";
	static final String NOT_BLANK_MESSAGE = "{0}은(는) 비어 있을 수 없습니다.";
	static final String REQUIRED_FIELD_MESSAGE = "{0}은(는) 필수 항목입니다.";
	static final String PASSWORD_MIN_LENGTH_MESSAGE = "은(는) 60자 이상이여야 합니다";
	static final String INVALID_ENUM_VALUE_MESSAGE = "{0} 값이 유효하지 않습니다. 허용값 {1}";
	static final String PASSWORD_KEYWORD = "비밀번호";

	public static void validateNotNullAndBlank(String value, String fieldName) {
		if (!StringUtils.hasText(value)) {
			String errorMessage = getFormatterMessage(NOT_BLANK_MESSAGE, fieldName);
			rejectInvalidField(errorMessage);
		}
	}

	public static void validateAccountPassword(String value, String fieldName) {
		validateNotNullAndBlank(value, fieldName);
		if (fieldName.equals(PASSWORD_KEYWORD)) {
			if (value.length() < 60) {
				String errorMessage = fieldName + PASSWORD_MIN_LENGTH_MESSAGE;
				rejectInvalidField(errorMessage);
			}
		}
	}

	public static <E extends Enum<E>> void validateRequiredEnum(E value, String fieldName) {
		if (value == null) {
			String errorMessage = getFormatterMessage(REQUIRED_FIELD_MESSAGE, fieldName);
			rejectInvalidField(errorMessage);
		}
	}

	public static void validatePositive(int value, String fieldName) {
		if (value <= 0) {
			String errorMessage = getFormatterMessage(POSITIVE_MESSAGE, fieldName);
			rejectInvalidField(errorMessage);
		}
	}

	public static void validatePositive(long value, String fieldName) {
		if (value <= 0) {
			String errorMessage = getFormatterMessage(POSITIVE_MESSAGE, fieldName);
			rejectInvalidField(errorMessage);
		}
	}

	public static void validateNonNegative(int value, String fieldName) {
		if (value < 0) {
			String errorMessage = getFormatterMessage(NOT_NEGATIVE_MESSAGE, fieldName);
			rejectInvalidField(errorMessage);
		}
	}

	private static String getFormatterMessage(String message, String fieldName) {
		return MessageFormat.format(message, fieldName);
	}

	private static void rejectInvalidField(String errorMessage) {
		throw new FieldValidationException(ErrorCode.INVALID_DOMAIN_FIELD, errorMessage);
	}
}
