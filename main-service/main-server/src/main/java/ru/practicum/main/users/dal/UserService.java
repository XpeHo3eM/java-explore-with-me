package ru.practicum.main.users.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.users.dto.NewUserDto;
import ru.practicum.main.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    List<UserDto> getAll(List<Long> ids, Pageable pageable);

    void delete(Long userId);
}