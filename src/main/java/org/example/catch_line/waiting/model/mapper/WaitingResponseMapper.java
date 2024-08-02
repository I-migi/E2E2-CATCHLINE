package org.example.catch_line.waiting.model.mapper;

import org.example.catch_line.waiting.model.dto.WaitingResponse;
import org.example.catch_line.waiting.model.entity.WaitingEntity;
import org.springframework.stereotype.Service;

@Service
public class WaitingResponseMapper {

	public WaitingResponse convertToResponse(WaitingEntity entity) {
		return WaitingResponse.builder()
			.waitingId(entity.getWaitingId())
			.memberCount(entity.getMemberCount())
			.status(entity.getStatus())
			.waitingType(entity.getWaitingType())
			.build();
	}

}
