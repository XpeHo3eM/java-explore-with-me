package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewStatDto;
import ru.practicum.stats.client.StatsClient;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.util.Constants.TIME_FORMAT;

@Controller
@RequiredArgsConstructor
@Validated
public class StatsClientController {
    private final StatsClient client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createHit(@RequestBody @Valid NewStatDto hitDto) {
        return client.createHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
                                           @RequestParam(name = "end") @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
                                           @RequestParam(name = "uris") Optional<List<String>> uris,
                                           @RequestParam(name = "unique") boolean unique) {
        return client.getStats(start, end, uris.orElse(Collections.emptyList()), unique);
    }
}
