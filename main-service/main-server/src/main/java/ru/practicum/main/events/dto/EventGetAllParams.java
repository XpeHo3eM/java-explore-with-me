package ru.practicum.main.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.main.events.enums.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Value
@Builder
public class EventGetAllParams {
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime start;
    LocalDateTime end;
    Boolean available;
    EventSort sort;
    int from;
    int size;
    HttpServletRequest httpServletRequest;
}
