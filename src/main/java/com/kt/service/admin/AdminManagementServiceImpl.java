package com.kt.service.admin;

import com.kt.domain.dto.request.AdminRequest;
import com.kt.domain.dto.response.AdminResponse;

import com.kt.domain.entity.AdminEntity;
import com.kt.repository.admin.AdminRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {
	private final AdminRepository adminRepository;

	@Override
	public AdminResponse.Detail detail() {
		AdminEntity admin = adminRepository.findByAdminCode(AdminEntity.SYSTEM_ADMIN_CODE);
		return new AdminResponse.Detail(
			admin.getName(),
			admin.getEmail()
		);
	}

	@Override
	@Transactional
	public void update(AdminRequest.Update request) {
		AdminEntity admin = adminRepository.findByAdminCode(AdminEntity.SYSTEM_ADMIN_CODE);
		admin.update(request.name(), request.email());
	}

}
