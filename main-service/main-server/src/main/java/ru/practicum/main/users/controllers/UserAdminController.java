package ru.practicum.main.users.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.users.dal.UserService;
import ru.practicum.main.users.dto.NewUserDto;
import ru.practicum.main.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.general.util.Constants.PAGE_DEFAULT_FROM;
import static ru.practicum.general.util.Constants.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserDto newUserDto) {
        return userService.create(newUserDto);
    }

    @GetMapping()
    public List<UserDto> get(@RequestParam Optional<List<Long>> ids,
                             @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                             @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        return userService.getAll(ids.orElse(Collections.emptyList()), page);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}