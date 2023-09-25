package ru.practicum.main.compilations.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationDto updateCompilationDto);

    void delete(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(Long id);
}