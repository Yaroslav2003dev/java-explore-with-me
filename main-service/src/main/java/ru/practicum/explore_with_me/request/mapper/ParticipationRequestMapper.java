package ru.practicum.explore_with_me.request.mapper;

import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatusRequest());
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(List<ParticipationRequest> participationRequestList) {
        List<ParticipationRequestDto> participationRequestDtoList = new ArrayList<>();
        for (ParticipationRequest participationRequest : participationRequestList) {
            participationRequestDtoList.add(toParticipationRequestDto(participationRequest));
        }
        return participationRequestDtoList;
    }


}
