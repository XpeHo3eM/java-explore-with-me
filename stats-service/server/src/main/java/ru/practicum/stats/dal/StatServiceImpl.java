package ru.practicum.stats.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dao.StatRepository;
import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.dto.NewStatDto;
import ru.practicum.stats.mapper.StatServiceMapper;
import ru.practicum.stats.model.StatItem;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private final StatServiceMapper mapper;

    @Override
    public void addHit(NewStatDto newStatDto) {
        repository.save(mapper.toStatItem(newStatDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HitsStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return repository.getStats(start, end, uris, unique);
    }
}
