package ru.practicum.main.category.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dao.CategoryRepository;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.handler.ConflictException;
import ru.practicum.main.handler.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        validateCategoryExists(newCategoryDto.getName());

        return mapper.toDto(categoryRepository.save(mapper.toCategory(newCategoryDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = getCategoryOrThrowException(id);

        return mapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        Category category = getCategoryOrThrowException(id);

        if (!newCategoryDto.getName().equals(category.getName())) {
            validateCategoryExists(newCategoryDto.getName());

            category.setName(newCategoryDto.getName());
        }

        return mapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Category category = getCategoryOrThrowException(id);

        if (eventRepository.existsByCategory(category)) {
            throw new ConflictException(String.format("Категория с id = %d имеет связанные события", id));
        }

        categoryRepository.deleteById(id);
    }

    private Category getCategoryOrThrowException(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id = %d не найдена", id)));
    }

    private void validateCategoryExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException(String.format("Категория с названием = %s уже создана", name));
        }
    }
}