package ru.practicum.main.events.model;


import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}