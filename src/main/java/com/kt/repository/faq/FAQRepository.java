package com.kt.repository.faq;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.entity.FAQEntity;

public interface FAQRepository extends JpaRepository<FAQEntity, UUID> {

}
