package ru.practicum.main.events.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dal.EventService;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventGetAllParams;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.enums.EventSort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.general.util.Constants.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable(value = "id") Long id,
                                           HttpServletRequest request) {
        return eventService.getByIdPublic(id, request);
    }

    @GetMapping
    public Collection<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
                                                     @RequestParam(defaultValue = "false") Boolean available,
                                                     @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                                     @RequestParam @PositiveOrZero Optional<Integer> from,
                                                     @RequestParam @Positive Optional<Integer> size,
                                                     HttpServletRequest request) {
        return eventService.getAllPublic(EventGetAllParams.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .start(start)
                .end(end)
                .available(available)
                .sort(sort)
                .from(from.orElse(PAGE_DEFAULT_FROM))
                .size(size.orElse(PAGE_DEFAULT_SIZE))
                .httpServletRequest(request)
                .build());
    }
}
