package ru.practicum.main.compilations.dto;

import lombok.Value;
import ru.practicum.main.events.dto.EventShortDto;

import java.util.List;

@Value
public class CompilationDto {
    long id;
    String title;
    boolean pinned;
    List<EventShortDto> events;
}