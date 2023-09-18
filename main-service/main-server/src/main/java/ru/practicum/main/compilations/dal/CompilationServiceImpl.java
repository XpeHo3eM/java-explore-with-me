package ru.practicum.main.compilations.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilations.dao.CompilationRepository;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.mapper.CompilationMapper;
import ru.practicum.main.compilations.model.Compilation;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.handler.EntityNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.toCompilation(newCompilationDto);

        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }

        return mapper.toDto(compilation);
    }

    @Override
    public CompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilationInRepository = getCompilationOrThrowException(compId);
        Set<Long> updatedEvents = newCompilationDto.getEvents();
        Boolean updatedPinned = newCompilationDto.getPinned();
        String updatedTitle = newCompilationDto.getTitle();

        if (updatedEvents != null) {
            compilationInRepository.setEvents(eventRepository.findAllById(updatedEvents));
        }
        if (updatedPinned != null) {
            compilationInRepository.setPinned(updatedPinned);
        }
        if (updatedTitle != null && !updatedTitle.isBlank()) {
            compilationInRepository.setTitle(updatedTitle);
        }

        return mapper.toDto(compilationInRepository);
    }

    @Override
    public void delete(Long compId) {
        isCompilationExists(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        return (pinned == null
                ? compilationRepository.findAll(pageable)
                : compilationRepository.findAllByPinned(pinned, pageable))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long id) {
        return mapper.toDto(getCompilationOrThrowException(id));
    }

    private Compilation getCompilationOrThrowException(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие с id = %d не найдено", id)));
    }

    private void isCompilationExists(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Событие с id = %d не найдено", id));
        }
    }
}