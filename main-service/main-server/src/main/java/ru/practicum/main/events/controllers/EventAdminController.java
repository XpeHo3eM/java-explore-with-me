package ru.practicum.main.events.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dal.EventService;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventUpdatedDto;
import ru.practicum.main.events.enums.EventState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.general.util.Constants.*;


@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<EventState> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) Boolean onlyPending,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, page, onlyPending);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable(value = "eventId") Long eventId,
                                         @RequestBody @Valid EventUpdatedDto eventUpdatedDto) {
        return eventService.updateByAdmin(eventId, eventUpdatedDto);
    }
}