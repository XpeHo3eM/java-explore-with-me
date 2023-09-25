package ru.practicum.main.users.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.handler.ConflictException;
import ru.practicum.main.handler.EntityNotFoundException;
import ru.practicum.main.users.dao.UserRepository;
import ru.practicum.main.users.dto.NewUserDto;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.mapper.UserMapper;
import ru.practicum.main.users.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto create(NewUserDto newUserDto) {
        validateEmailExists(newUserDto.getEmail());

        return mapper.toDto(userRepository.save(mapper.toUser(newUserDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        Page<User> users = ids.isEmpty()
                ? userRepository.findAll(pageable)
                : userRepository.findAllByIdIn(ids, pageable);

        return users
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        userRepository.deleteById(userId);
    }

    private void validateEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("Пользователь с email = %s уже зарегистрирован", email));
        }
    }
}