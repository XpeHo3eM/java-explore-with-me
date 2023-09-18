package ru.practicum.main.events.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;
import static ru.practicum.main.events.enums.EventState.PENDING;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120)
    private String title;

    @Column(length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 7000)
    private String description;

    @Column(name = "event_date")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    @Column(name = "location_lat")
    private float lat;

    @Column(name = "location_lon")
    private float lon;

    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state = PENDING;

    @Column(name = "created_on")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime publishedOn;

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