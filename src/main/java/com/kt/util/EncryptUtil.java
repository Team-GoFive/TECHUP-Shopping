package com.kt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptUtil {

	private static final String ALGO = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int GCM_TAG_LENGTH = 128;
	private static final int IV_LENGTH = 12;

	private static SecretKey secretKey;

	public static void loadKey(String key) {
		if (key == null) {
			throw new IllegalStateException("AES 키가 존재하지 않습니다.");
		}

		byte[] keyBytes = key.trim().getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length != 32) {
			throw new IllegalStateException("AES-256 키는 32바이트여야 합니다.");
		}

		secretKey = new SecretKeySpec(keyBytes, ALGO);
	}

	public static String encrypt(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);

			byte[] iv = new byte[IV_LENGTH];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);

			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			byte[] result = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, result, 0, iv.length);
			System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(result);

		} catch (Exception e) {
			throw new RuntimeException("암호화 실패", e);
		}
	}

	public static String decrypt(String cipherText) {
		try {
			byte[] decoded = Base64.getDecoder().decode(cipherText);

			byte[] iv = new byte[IV_LENGTH];
			System.arraycopy(decoded, 0, iv, 0, iv.length);

			byte[] encrypted = new byte[decoded.length - iv.length];
			System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

			byte[] plain = cipher.doFinal(encrypted);
			return new String(plain, StandardCharsets.UTF_8);

		} catch (Exception e) {
			throw new RuntimeException("복호화 실패", e);
		}
	}


}
