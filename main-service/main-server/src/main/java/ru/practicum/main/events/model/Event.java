package ru.practicum.main.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.main.events.enums.EventState.PENDING;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "location_lat")
    private Double lat;

    @Column(name = "location_lon")
    private Double lon;

    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Enumerated(EnumType.STRING)
    private EventState state = PENDING;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}