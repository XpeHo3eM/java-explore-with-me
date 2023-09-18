package ru.practicum.main.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.users.dto.NewUserDto;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    UserShortDto toShortDto(User user);

    User toUser(NewUserDto newUserDto);
}
