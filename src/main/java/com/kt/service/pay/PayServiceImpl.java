package com.kt.service.pay;

import com.kt.domain.dto.response.PayResponse;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

	private final UserRepository userRepository;

	@Override
	public PayResponse.Balance getBalance(UUID userId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		PayEntity pay = user.getPay();
		return PayResponse.Balance.from(pay);
	}

}
