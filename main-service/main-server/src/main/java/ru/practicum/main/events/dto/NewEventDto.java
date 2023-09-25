package ru.practicum.main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewEventDto {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    private EventState state = EventState.PENDING;
}