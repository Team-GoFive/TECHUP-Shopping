package com.kt.repository.admin;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.AdminEntity;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {

	default AdminEntity findByIdOrThrow(UUID adminId) {
		return findById(adminId).orElseThrow(
			() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND)
		);
	}

	AdminEntity findByAdminCode(String adminCode);
}
