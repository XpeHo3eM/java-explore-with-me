package ru.practicum.main.compilations.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilations.dal.CompilationService;
import ru.practicum.main.compilations.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.general.util.Constants.PAGE_DEFAULT_FROM;
import static ru.practicum.general.util.Constants.PAGE_DEFAULT_SIZE;


@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService serviceCompilation;

    @GetMapping("/{compId}")
    public CompilationDto getByIdPublic(@PathVariable Long compId) {
        return serviceCompilation.getById(compId);
    }

    @GetMapping
    public Collection<CompilationDto> get(@RequestParam(required = false) Boolean pinned,
                                          @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return serviceCompilation.getAll(pinned, page);
    }
}