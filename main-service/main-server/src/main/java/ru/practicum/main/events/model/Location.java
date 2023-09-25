package ru.practicum.main.events.model;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Location {
    private Double lat;
    private Double lon;
}