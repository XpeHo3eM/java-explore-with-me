package ru.practicum.main.requests.dal;

import ru.practicum.main.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long eventId, Long userId);

    List<RequestDto> getAll(Long userId);

    RequestDto cancel(Long requestId, Long userId);
}