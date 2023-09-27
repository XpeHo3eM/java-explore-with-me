package ru.practicum.main.compilations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.main.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}