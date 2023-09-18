package ru.practicum.main.events.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime start, LocalDateTime end, Pageable pageable);

    EventFullDto updateByAdmin(Long eventId, EventUpdatedDto eventUpdateDto);

    EventFullDto create(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllByUser(Long userId, Pageable pageable);

    EventFullDto getByIdPrivate(Long userId, Long eventId);

    EventFullDto updateByUser(Long userId, Long eventId, EventUpdatedDto eventDto);

    List<EventShortDto> getAllPublic(EventGetAllParams eventGetAllParams);

    EventFullDto getByIdPublic(Long id, HttpServletRequest request);

    List<ParticipationRequestDto> getParticipationRequestPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest dtoRequest);
}