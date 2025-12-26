package com.kt.domain.entity;

import com.kt.constant.FAQCategory;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "faq")
public class FAQEntity extends BaseEntity {

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	private FAQCategory category;
	private String fileId;

	private FAQEntity(String title, String content, FAQCategory category, String fileId) {
		this.title = title;
		this.content = content;
		this.category = category;
		this.fileId = fileId;
	}

	public static FAQEntity create(String title, String content, FAQCategory category, String fileId) {
		return new FAQEntity(title, content, category, null);
	}

	public void updateFileId(String fileId) {
		this.fileId = fileId;
	}

}
