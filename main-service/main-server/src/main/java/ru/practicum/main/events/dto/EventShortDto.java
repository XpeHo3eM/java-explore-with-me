package ru.practicum.main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    private Boolean paid;
    private Integer confirmedRequests;
    private Long views;
    private UserShortDto initiator;
}