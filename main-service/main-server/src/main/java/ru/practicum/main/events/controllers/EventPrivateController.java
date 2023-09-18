package ru.practicum.main.events.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dal.EventService;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.dto.EventUpdatedDto;
import ru.practicum.main.events.dto.NewEventDto;
import ru.practicum.main.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.Optional;

import static ru.practicum.general.util.Constants.PAGE_DEFAULT_FROM;
import static ru.practicum.general.util.Constants.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto eventDto) {
        return service.create(userId, eventDto);
    }

    @GetMapping
    public Collection<EventShortDto> getAllByUserId(@PathVariable(value = "userId") Long userId,
                                                    @RequestParam @PositiveOrZero Optional<Integer> from,
                                                    @RequestParam @Positive Optional<Integer> size) {
        Integer pageFrom = from.orElse(PAGE_DEFAULT_FROM);
        Integer pageSize = size.orElse(PAGE_DEFAULT_SIZE);
        PageRequest page = PageRequest.of(pageFrom / pageSize, pageSize);

        return service.getAllByUser(userId, page);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable Long userId,
                                @PathVariable Long eventId) {
        return service.getByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid EventUpdatedDto eventDto) {
        return service.updateByUser(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getParticipationRequest(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        return service.getParticipationRequestPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return service.updateEventRequestStatusPrivate(userId, eventId, updateRequest);
    }
}