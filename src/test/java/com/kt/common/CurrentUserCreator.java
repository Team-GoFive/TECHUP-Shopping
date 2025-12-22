package com.kt.common;

import java.util.UUID;

import com.kt.constant.AccountRole;
import com.kt.security.DefaultCurrentUser;

public class CurrentUserCreator {

	public static DefaultCurrentUser getAdminUserDetails(String email) {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			email,
			AccountRole.ADMIN
		);
	}

	public static DefaultCurrentUser getAdminUserDetails() {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			"admin@naver.com",
			AccountRole.ADMIN
		);
	}

	public static DefaultCurrentUser getAdminUserDetails(UUID adminId) {
		return new DefaultCurrentUser(
			adminId,
			"admin@naver.com",
			AccountRole.ADMIN
		);
	}

	public static DefaultCurrentUser getMemberUserDetails() {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			"member@naver.com",
			AccountRole.MEMBER
		);
	}

	public static DefaultCurrentUser getMemberUserDetails(UUID userId) {
		return new DefaultCurrentUser(
			userId,
			"member@naver.com",
			AccountRole.MEMBER
		);
	}

	public static DefaultCurrentUser getMemberUserDetails(String email) {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			email,
			AccountRole.MEMBER
		);
	}

	public static DefaultCurrentUser getSellerUserDetails() {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			"seller@naver.com",
			AccountRole.SELLER
		);
	}

	public static DefaultCurrentUser getSellerUserDetails(UUID sellerId) {
		return new DefaultCurrentUser(
			sellerId,
			"seller@naver.com",
			AccountRole.SELLER
		);
	}

	public static DefaultCurrentUser getSellerUserDetails(String email) {
		return new DefaultCurrentUser(
			UUID.randomUUID(),
			email,
			AccountRole.SELLER
		);
	}

	public static DefaultCurrentUser getCourierUserDetails(UUID courierId) {
		return new DefaultCurrentUser(
			courierId,
			"courier@naver.com",
			AccountRole.COURIER
		);
	}

}
