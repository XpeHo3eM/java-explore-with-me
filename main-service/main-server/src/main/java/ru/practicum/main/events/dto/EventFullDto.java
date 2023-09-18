package ru.practicum.main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.model.Location;
import ru.practicum.main.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String description;
    private String annotation;
    private CategoryDto category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime publishedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime createdOn;

    private Location location;
    private boolean paid;
    private int participantLimit;
    private int confirmedRequests;
    private long views;
    private EventState state;
    private boolean requestModeration;
    private UserShortDto initiator;
}