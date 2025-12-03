package com.kt.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.domain.entity.CourierEntity;
import com.kt.repository.courier.CourierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourierServiceImpl implements CourierService {

	private final CourierRepository courierRepository;

	@Override
	public CourierResponse.Detail getDetail(UUID courierId) {
		CourierEntity courierEntity = courierRepository.findByCourierIdOrThrow(courierId);
		return new CourierResponse.Detail(
			courierEntity.getId(),
			courierEntity.getName(),
			courierEntity.getEmail(),
			courierEntity.getGender(),
			courierEntity.getWorkStatus()
		);
	}

	@Override
	public CourierResponse.DetailAdmin getDetailForAdmin(UUID courierId) {
		CourierEntity courierEntity = courierRepository.findByCourierIdOrThrow(courierId);
		return new CourierResponse.DetailAdmin(
			courierEntity.getId(),
			courierEntity.getName(),
			courierEntity.getEmail(),
			courierEntity.getGender(),
			courierEntity.getStatus(),
			courierEntity.getWorkStatus()
		);
	}

	@Override
	public void updateDetail(UUID courierId, CourierRequest.UpdateDetails details) {
		CourierEntity courier = courierRepository.findByCourierIdOrThrow(courierId);
		courier.updateDetails(
			details.name(),
			details.gender()
		);
	}
}
