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
import java.util.Optional;

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
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
                                              @RequestParam @PositiveOrZero Optional<Integer> from,
                                              @RequestParam @Positive Optional<Integer> size) {
        Integer pageFrom = from.orElse(PAGE_DEFAULT_FROM);
        Integer pageSize = size.orElse(PAGE_DEFAULT_SIZE);
        PageRequest page = PageRequest.of(pageFrom / pageSize, pageSize);

        return eventService.getAllByAdmin(users, states, categories, start, end, page);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable(value = "eventId") Long eventId,
                                         @RequestBody @Valid EventUpdatedDto eventUpdatedDto) {
        return eventService.updateByAdmin(eventId, eventUpdatedDto);
    }
}