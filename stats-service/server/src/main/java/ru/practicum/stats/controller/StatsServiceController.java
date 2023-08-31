package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dal.StatService;
import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.dto.NewStatDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.stats.util.Constants.TIME_FORMAT;

@RestController
@RequiredArgsConstructor
public class StatsServiceController {
    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRequest(@RequestBody NewStatDto newStatDto) {
        service.addHit(newStatDto);
    }

    @GetMapping("/stats")
    public Collection<HitsStatDto> getStats(@RequestParam @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
                                            @RequestParam @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
                                            @RequestParam Optional<List<String>> uris,
                                            @RequestParam (defaultValue = "false") boolean unique) {
        return service.getStats(start, end, uris.orElse(null), unique);
    }
}
