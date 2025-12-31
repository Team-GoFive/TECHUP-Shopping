package com.kt.chat.event;

import java.util.UUID;

public record HandoverEvent(
	UUID userId,
	String lastQuestion,
	String conversationId
) {
}
