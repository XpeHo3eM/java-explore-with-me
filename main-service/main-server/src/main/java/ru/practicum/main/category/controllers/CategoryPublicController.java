package ru.practicum.main.category.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dal.CategoryService;
import ru.practicum.main.category.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.general.util.Constants.PAGE_DEFAULT_FROM;
import static ru.practicum.general.util.Constants.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return categoryService.getAll(page);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}