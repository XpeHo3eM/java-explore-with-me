package ru.practicum.main.events.model;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Location {
    private Double lat;
    private Double lon;
}