package ru.practicum.dal;

import ru.practicum.dto.HitsStatDto;
import ru.practicum.dto.NewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(NewStatDto newStatDto);

    List<HitsStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
