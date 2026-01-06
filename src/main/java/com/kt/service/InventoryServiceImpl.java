package com.kt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.InventoryEntity;
import com.kt.exception.CustomException;
import com.kt.repository.inventory.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;

	@Override
	@Transactional
	public void reduceStock(List<OrderRequest.Item> items) {
		for (OrderRequest.Item item : items) {
			InventoryEntity inventory = inventoryRepository.findByProductIdWithLockOrThrow(item.productId());

			if (inventory.getStock() < item.quantity()) {
				throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
			}
			inventory.decreaseStock(item.quantity());
		}
	}
}
