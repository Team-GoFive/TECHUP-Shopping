package com.kt.security;

import java.util.UUID;

import com.kt.constant.AccountRole;

public interface CurrentUser {
	UUID getId();

	AccountRole getRole();
}
