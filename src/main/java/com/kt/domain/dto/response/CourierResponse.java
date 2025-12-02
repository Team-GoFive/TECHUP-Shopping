package com.kt.domain.dto.response;

import java.util.UUID;

import com.kt.constant.CourierWorkStatus;
import com.kt.constant.Gender;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourierResponse {
	@Schema(name="CourierResponse.Search")
	public record Search(
		UUID id,
		String name,
		String email,
		Gender gender,
		CourierWorkStatus status
	) {
		@QueryProjection
		public Search {
		}
	}
	@Schema(name="CourierResponse.Detail")
	public record Detail(
		UUID id,
		String name,
		String email,
		Gender gender,
		CourierWorkStatus status
	){
		@QueryProjection
		public Detail{
		}
	}
}
