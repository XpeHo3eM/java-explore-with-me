package ru.practicum.main.compilations.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilations.dao.CompilationRepository;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;
import ru.practicum.main.compilations.mapper.CompilationMapper;
import ru.practicum.main.compilations.model.Compilation;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.handler.EntityNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        List<Event> events;
        if (newCompilationDto.getEvents() != null) {
            events = newCompilationDto.getEvents()
                    .stream()
                    .map(this::getEventOrThrowException)
                    .collect(Collectors.toList());
        } else {
            events = Collections.emptyList();
        }

        compilation.setEvents(events);

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilationInRepository = getCompilationOrThrowException(compId);
        Set<Long> updatedEvents = updateCompilationDto.getEvents();
        Boolean updatedPinned = updateCompilationDto.getPinned();
        String updatedTitle = updateCompilationDto.getTitle();

        if (updatedEvents != null) {
            compilationInRepository.setEvents(eventRepository.findAllById(updatedEvents));
        }
        if (updatedPinned != null) {
            compilationInRepository.setPinned(updatedPinned);
        }
        if (updatedTitle != null && !updatedTitle.isBlank()) {
            compilationInRepository.setTitle(updatedTitle);
        }

        return compilationMapper.toDto(compilationInRepository);
    }

    @Override
    public void delete(Long compId) {
        validateCompilationExists(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        Page<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable);
        }

        return compilations
                .stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long id) {
        return compilationMapper.toDto(getCompilationOrThrowException(id));
    }

    private Compilation getCompilationOrThrowException(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка событий с id = %d не найдена", id)));
    }

    private void validateCompilationExists(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Подборка событий с id = %d не найдена", id));
        }
    }

    private Event getEventOrThrowException(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие с id = %d не найдено", id)));
    }
}