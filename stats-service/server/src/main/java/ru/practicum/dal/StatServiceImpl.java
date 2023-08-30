package ru.practicum.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dao.StatRepository;
import ru.practicum.dto.HitsStatDto;
import ru.practicum.dto.NewStatDto;
import ru.practicum.mapper.StatServiceMapper;
import ru.practicum.model.StatItem;

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
        StatItem hit = mapper.toStatItem(newStatDto);

        repository.save(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HitsStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return repository.getStats(start, end, uris, unique);
    }
}
