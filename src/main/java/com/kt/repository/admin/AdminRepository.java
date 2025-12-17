package com.kt.repository.admin;

import com.kt.domain.entity.AdminEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {

	Optional<AdminEntity> findByEmail(String email);
}
