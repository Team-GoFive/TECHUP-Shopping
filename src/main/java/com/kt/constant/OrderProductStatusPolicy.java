package com.kt.constant;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class OrderProductStatusPolicy {
	private static final Map<OrderProductStatus, Set<OrderProductStatus>> FORCE_CHANGE_ALLOWED_MAP =
		new EnumMap<>(OrderProductStatus.class);

	static {
		FORCE_CHANGE_ALLOWED_MAP.put(
			OrderProductStatus.SHIPPING_READY,
			EnumSet.of(
				OrderProductStatus.SHIPPING,
				OrderProductStatus.CANCELED
			)
		);

		FORCE_CHANGE_ALLOWED_MAP.put(
			OrderProductStatus.SHIPPING,
			EnumSet.of(
				OrderProductStatus.SHIPPING_COMPLETED
			)
		);

	}

	private OrderProductStatusPolicy() {}

	public static boolean canForceChange(
		OrderProductStatus current,
		OrderProductStatus target
	) {
		return FORCE_CHANGE_ALLOWED_MAP
			.getOrDefault(current, Set.of())
			.contains(target);
	}
}
