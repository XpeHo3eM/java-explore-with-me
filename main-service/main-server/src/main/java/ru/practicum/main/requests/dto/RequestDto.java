package ru.practicum.main.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.requests.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private Long requester;
    private Long event;
    private RequestStatus status;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime created;
}