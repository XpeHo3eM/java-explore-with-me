package ru.practicum.main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.events.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotEmpty
    @Size(min = 3, max = 120)
    private String title;

    @NotEmpty
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotEmpty
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    private Location location;

    @NotNull
    private Boolean paid;

    @NotNull
    @PositiveOrZero
    private Integer participantLimit;

    @NotNull
    private Boolean requestModeration;
}