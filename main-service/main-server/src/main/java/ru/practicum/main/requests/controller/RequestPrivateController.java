package ru.practicum.main.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.dal.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@RequestParam @Positive Long eventId,
                                          @PathVariable @Positive Long userId) {
        return service.create(eventId, userId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable @Positive Long userId) {
        return service.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable @Positive Long requestId,
                                          @PathVariable @Positive Long userId) {
        return service.cancel(requestId, userId);
    }
}