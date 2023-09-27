package ru.practicum.main.compilations.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class NewCompilationDto {
    @NotBlank
    @Size(max = 50)
    private String title;

    private Boolean pinned = false;
    private Set<Long> events;
}