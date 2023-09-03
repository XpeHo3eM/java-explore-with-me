package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.NewStatDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.stats.util.Constants.TIME_FORMAT;

@Controller
@RequiredArgsConstructor
@Validated
public class StatsClientController {
    private final StatsClient client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createHit(@RequestBody NewStatDto hitDto) {
        return client.createHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
                                           @RequestParam Optional<List<String>> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return client.getStats(start, end, uris.orElse(null), unique);
    }
}
