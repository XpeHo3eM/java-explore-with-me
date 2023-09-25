package ru.practicum.main.events.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dal.EventService;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

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
    public EventFullDto create(@PathVariable @Positive Long userId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        return service.create(userId, newEventDto);
    }

    @GetMapping
    public Collection<EventShortDto> getAllByUserId(@PathVariable @Positive Long userId,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return service.getAllByUser(userId, page);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable @Positive Long userId,
                                @PathVariable @Positive Long eventId) {
        return service.getByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId,
                               @RequestBody @Valid EventUpdatedDto eventDto) {
        return service.updateByUser(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<RequestDto> getParticipationRequest(@PathVariable @Positive Long userId,
                                                          @PathVariable @Positive Long eventId) {
        return service.getRequestsPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult updateRequestStatus(@PathVariable @Positive Long userId,
                                                         @PathVariable @Positive Long eventId,
                                                         @RequestBody @Valid RequestStatusUpdateRequest updateRequest) {
        return service.updateEventRequestStatusPrivate(userId, eventId, updateRequest);
    }
}