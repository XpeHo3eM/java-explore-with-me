package ru.practicum.main.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.requests.dal.RequestService;
import ru.practicum.main.requests.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@RequestParam Long eventId,
                             @PathVariable Long userId) {
        return service.create(eventId, userId);
    }

    @GetMapping
    public List<RequestDto> getAll(@PathVariable Long userId) {
        return service.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long requestId,
                             @PathVariable Long userId) {
        return service.cancel(requestId, userId);
    }
}