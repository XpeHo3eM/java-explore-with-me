package ru.practicum.main.compilations.dto;

import lombok.Value;

import javax.validation.constraints.Size;
import java.util.Set;

@Value
public class UpdateCompilationDto {
    @Size(min = 1, max = 50)
    String title;

    Boolean pinned;
    Set<Long> events;
}
