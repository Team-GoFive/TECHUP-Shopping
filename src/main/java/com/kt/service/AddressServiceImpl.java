package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.dto.response.AddressResponse;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public UUID create(UUID userId, AddressRequest request) {
		UserEntity user = userRepository.findByIdOrThrow(userId);

		AddressEntity address = AddressEntity.create(
			request.receiverName(),
			request.receiverMobile(),
			request.city(),
			request.district(),
			request.roadAddress(),
			request.detail(),
			user
		);

		return addressRepository.save(address).getId();
	}

	@Override
	public List<AddressResponse> getMyAddresses(UUID userId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);

		return addressRepository.findAllByCreatedBy(user)
			.stream()
			.map(address -> new AddressResponse(
				address.getId(),
				address.getReceiverName(),
				address.getReceiverMobile(),
				address.getCity(),
				address.getDistrict(),
				address.getRoadAddress(),
				address.getDetail()
			))
			.toList();
	}

	@Override
	public AddressResponse getOne(UUID userId, UUID addressId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);

		AddressEntity address = addressRepository.findByIdAndCreatedByOrThrow(addressId, user);

		return new AddressResponse(
			address.getId(),
			address.getReceiverName(),
			address.getReceiverMobile(),
			address.getCity(),
			address.getDistrict(),
			address.getRoadAddress(),
			address.getDetail()
		);
	}

	@Override
	@Transactional
	public void update(UUID userId, UUID addressId, AddressRequest request) {
		UserEntity user = userRepository.findByIdOrThrow(userId);

		AddressEntity address = addressRepository.findByIdAndCreatedByOrThrow(addressId, user);

		address.update(
			request.receiverName(),
			request.receiverMobile(),
			request.city(),
			request.district(),
			request.roadAddress(),
			request.detail()
		);
	}

	@Override
	@Transactional
	public void delete(UUID userId, UUID addressId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);

		AddressEntity address = addressRepository.findByIdAndCreatedByOrThrow(addressId, user);

		addressRepository.delete(address);
	}
}
