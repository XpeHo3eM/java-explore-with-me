package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.NewStatDto;
import ru.practicum.stats.model.StatItem;

@Mapper(componentModel = "spring")
public interface StatServiceMapper {
    StatItem toStatItem(NewStatDto newStatDto);
}