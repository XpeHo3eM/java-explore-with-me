package ru.practicum.main.users.dto;

import lombok.Value;


@Value
public class UserDto {
    Long id;
    String email;
    String name;
}