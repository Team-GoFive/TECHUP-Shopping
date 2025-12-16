package com.kt.repository.user;

import com.kt.constant.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.response.UserResponse;

public interface UserRepositoryCustom {
	Page<UserResponse.Search> searchUsers(Pageable pageable, String keyword, AccountRole role);
}
