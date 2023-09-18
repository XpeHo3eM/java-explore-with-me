package ru.practicum.main.requests.dal;

import ru.practicum.main.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequestByUserId(Long userId);

    ParticipationRequestDto updateStatusParticipationRequest(Long userId, Long requestId);
}