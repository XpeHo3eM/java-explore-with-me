package ru.practicum.main.events.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.main.events.enums.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
public class EventGetAllParams {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime start;
    private LocalDateTime end;
    private Boolean available;
    private EventSort sort;
    private int from;
    private int size;
    private HttpServletRequest httpServletRequest;
}
