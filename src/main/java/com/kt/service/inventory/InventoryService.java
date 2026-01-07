package com.kt.service.inventory;

import java.util.List;

import com.kt.domain.dto.request.OrderRequest;

public interface InventoryService {
	void reduceStock(List<OrderRequest.Item> items);
}
