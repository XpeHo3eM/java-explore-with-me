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
    private String annotation;
    private CategoryDto category;
    private String description;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserShortDto initiator;
    private EventState state;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime publishedOn;

    private Integer confirmedRequests;
    private long views;
}