package com.kt.security;

import java.util.Map;

public final class SecurityPath {

	public static final String[] PUBLIC = {
		"/api/auth/**",
		"/api/health/**",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		// view
		"/index.html",
		"/main.css",
		"/main.js",
		"/login.**",
		"/ws/**",

	};
	public static final String[] AUTHENTICATED = {
		"/api/ai/**",
		"/api/matches/**",
		"/pub/**",
		"/sub/**",
	};
	public static final String[] MEMBER = {
		"/api/orders/**",
		"/api/addresses/**"
	};
	public static final String[] ADMIN = {
		"/api/admin/**"
	};
	public static final String[] COURIER = {
		"/api/couriers/**"
	};
	public static final String[] SELLER = {
		"/api/seller/**"
	};
	public static final Map<String, String[]> ROLE_PATHS = Map.of(
		"ROLE_MEMBER", MEMBER,
		"ROLE_ADMIN", ADMIN,
		"ROLE_COURIER", COURIER,
		"ROLE_SELLER", SELLER
	);

	private SecurityPath() {
	}
}