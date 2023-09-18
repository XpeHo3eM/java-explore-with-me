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
import java.util.Optional;

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
    public Collection<CompilationDto> get(@RequestParam Optional<Boolean> pinned,
                                          @RequestParam @PositiveOrZero Optional<Integer> from,
                                          @RequestParam @Positive Optional<Integer> size) {
        Integer pageFrom = from.orElse(PAGE_DEFAULT_FROM);
        Integer pageSize = size.orElse(PAGE_DEFAULT_SIZE);
        PageRequest page = PageRequest.of(pageFrom / pageSize, pageSize);

        return serviceCompilation.getAll(pinned.orElse(false), page);
    }

}