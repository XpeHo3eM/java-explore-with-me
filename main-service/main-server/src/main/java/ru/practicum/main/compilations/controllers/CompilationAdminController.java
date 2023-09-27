package ru.practicum.main.compilations.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilations.dal.CompilationService;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateById(@PathVariable(value = "compId") Long compId,
                                     @RequestBody @Valid UpdateCompilationDto updateCompilationDto) {
        return compilationService.update(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "compId") Long compId) {
        compilationService.delete(compId);
    }
}