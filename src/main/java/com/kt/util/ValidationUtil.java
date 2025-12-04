package com.kt.util;

import java.text.MessageFormat;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.FieldValidationException;

import org.springframework.util.StringUtils;

public class ValidationUtil {

	static final String POSITIVE_MESSAGE = "{0}은(는) 0보다 커야합니다.";
	static final String NOT_BLANK_MESSAGE = "{0}은(는) 비어 있을 수 없습니다.";
	static final String NOT_NULL_MESSAGE = "{0}은(는) null 일 수 없습니다.";
	static final String PASSWORD_MIN_LENGTH_MESSAGE = "은(는) 60자 이상이여야 합니다";
	static final String INVALID_ENUM_VALUE_MESSAGE = "{0} 값이 유효하지 않습니다. 허용값 {1}";

	public static void validateNotNullAndBlank(String value, String fieldName) {
		if (!StringUtils.hasText(value)) {
			String errorMessage = getFormatterMessage(NOT_BLANK_MESSAGE, fieldName);
			throw new FieldValidationException(
				ErrorCode.INVALID_DOMAIN_FIELD, errorMessage
			);
		}
	}

	public static void validateCollectPassword(String value, String fieldName) {
		validateNotNullAndBlank(value, fieldName);

		if (fieldName.equals("유저 비밀번호")) {
			if (value.length() < 60) {
				String errorMessage = fieldName + PASSWORD_MIN_LENGTH_MESSAGE;
				throw new FieldValidationException(
					ErrorCode.INVALID_DOMAIN_FIELD, errorMessage
				);
			}
		}
	}

	public static <E extends Enum<E>> void validationNotNullEnum(E value, String fieldName) {
		if (value == null) {
			String errorMessage = getFormatterMessage(fieldName, NOT_NULL_MESSAGE);
			throw new FieldValidationException(
				ErrorCode.INVALID_DOMAIN_FIELD, errorMessage
			);
		}
	}

	public static void validatePositive(int value, String fieldName) {
		if (value <= 0) {
			String errorMessage = getFormatterMessage(POSITIVE_MESSAGE, fieldName);
			throw new FieldValidationException(ErrorCode.INVALID_DOMAIN_FIELD, errorMessage);
		}
	}

	private static String getFormatterMessage(String message, String fieldName) {
		return MessageFormat.format(message, fieldName);
	}

}
