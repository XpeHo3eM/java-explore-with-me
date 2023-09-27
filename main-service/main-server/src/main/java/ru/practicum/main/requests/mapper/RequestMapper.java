package ru.practicum.main.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.requests.dto.RequestDto;
import ru.practicum.main.requests.enums.RequestStatus;
import ru.practicum.main.requests.model.Request;
import ru.practicum.main.users.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "requester", source = "request.requester.id")
    @Mapping(target = "event", source = "request.event.id")
    RequestDto toDto(Request request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "user")
    Request toRequest(Event event, User user, RequestStatus status, LocalDateTime created);
}