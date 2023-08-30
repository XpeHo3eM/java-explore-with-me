package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.NewStatDto;
import ru.practicum.model.StatItem;

@Mapper(componentModel = "spring")
public interface StatServiceMapper {
    StatItem toStatItem(NewStatDto newStatDto);
}