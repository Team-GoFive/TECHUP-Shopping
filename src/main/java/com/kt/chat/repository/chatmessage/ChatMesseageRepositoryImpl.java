package com.kt.chat.repository.chatmessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kt.chat.domain.entity.ChatMessageEntity;
import com.kt.chat.domain.entity.QChatMessageEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMesseageRepositoryImpl implements ChatMessageRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QChatMessageEntity chatMessage = QChatMessageEntity.chatMessageEntity;

	@Override
	public Page<ChatMessageEntity> findByConversationIdWithCursor(
		Pageable pageable,
		UUID conversationId,
		Instant cursor
	) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(eqConversationId(conversationId));
		booleanBuilder.and(ltCursor(cursor));

		List<ChatMessageEntity> content = jpaQueryFactory
			.selectFrom(chatMessage)
			.where(booleanBuilder)
			.orderBy(chatMessage.createdAt.desc())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory
			.select(chatMessage.id)
			.from(chatMessage)
			.where(eqConversationId(conversationId))
			.fetch()
			.size();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression eqConversationId(UUID conversationId) {
		if (conversationId == null) {
			return null;
		}
		return chatMessage.conversationId.eq(conversationId);
	}

	private BooleanExpression ltCursor(Instant cursor) {
		if (cursor == null) {
			return null;
		}
		return chatMessage.createdAt.lt(cursor);
	}

}
