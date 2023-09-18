package ru.practicum.main.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.dto.NewEventDto;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.model.Location;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.mapper.UserMapper;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public abstract class EventMapper {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    public abstract Event toEvent(NewEventDto eventDto, User initiator, Category category);

    @Mapping(target = "location", source = "event", qualifiedByName = "toLocation")
    @Mapping(target = "category", source = "event.category", qualifiedByName = "toCategoryDto")
    @Mapping(target = "initiator", source = "event.initiator", qualifiedByName = "toUserShortDto")
    public abstract EventFullDto toFullDto(Event event);

    @Mapping(target = "category", source = "event.category", qualifiedByName = "toCategoryDto")
    @Mapping(target = "initiator", source = "event.initiator", qualifiedByName = "toUserShortDto")
    public abstract EventShortDto toShortDto(Event event);

    @Named("toLocation")
    Location toLocation(Event event) {
        return Location.builder()
                .lat(event.getLat())
                .lon(event.getLon())
                .build();
    }

    @Named("toCategoryDto")
    CategoryDto toCategoryDto(Category category) {
        return categoryMapper.toDto(category);
    }

    @Named("toUserShortDto")
    UserShortDto toUserShortDto(User user) {
        return userMapper.toShortDto(user);
    }
}