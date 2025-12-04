package com.kt.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.UserRole;
import com.kt.domain.dto.response.UserResponse;

public interface UserRepositoryCustom {
	Page<UserResponse.Search> searchUsers(Pageable pageable, String keyword, UserRole role);
}
