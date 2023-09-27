package ru.practicum.main.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.dto.NewEventDto;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.model.Location;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public abstract class EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "lat", source = "eventDto.location.lat")
    @Mapping(target = "lon", source = "eventDto.location.lon")
    @Mapping(target = "confirmedRequests", constant = "0")
    public abstract Event toEvent(NewEventDto eventDto, User initiator, Category category);

    @Mapping(target = "location", source = "event", qualifiedByName = "toLocation")
    public abstract EventFullDto toFullDto(Event event);

    public abstract EventShortDto toShortDto(Event event);

    @Named("toLocation")
    Location toLocation(Event event) {
        return Location.builder()
                .lat(event.getLat())
                .lon(event.getLon())
                .build();
    }
}