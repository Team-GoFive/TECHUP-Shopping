package com.kt.repository.courier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.CourierWorkStatus;
import com.kt.domain.dto.response.CourierResponse;

public interface CourierRepositoryCustom {
	Page<CourierResponse.Search> searchCouriers(Pageable pageable, String keyword, CourierWorkStatus workStatus);
}