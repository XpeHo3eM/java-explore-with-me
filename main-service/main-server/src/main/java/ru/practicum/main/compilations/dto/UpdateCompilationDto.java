package ru.practicum.main.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;
    private Set<Long> events;
}
