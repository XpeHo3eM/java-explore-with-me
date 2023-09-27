package ru.practicum.main.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.main.requests.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;


@Value
public class RequestDto {
    Long id;
    Long requester;
    Long event;
    RequestStatus status;

    @JsonFormat(pattern = TIME_FORMAT)
    LocalDateTime created;
}