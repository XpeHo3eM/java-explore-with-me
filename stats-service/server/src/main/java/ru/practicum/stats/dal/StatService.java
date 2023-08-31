package ru.practicum.stats.dal;

import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.dto.NewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(NewStatDto newStatDto);

    List<HitsStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
