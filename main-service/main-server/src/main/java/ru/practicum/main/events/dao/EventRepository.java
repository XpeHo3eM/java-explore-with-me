package ru.practicum.main.events.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategory(Category category);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e" +
            " FROM Event AS e" +
            " WHERE e.eventDate > :start" +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND (e.initiator.id IN :users OR :users IS NULL)" +
            "     AND (e.state IN :states OR :states IS NULL)")
    Page<Event> findAllForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime start, Pageable pageable);

    @Query("SELECT e" +
            " FROM Event e" +
            " WHERE e.state = :state" +
            "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests) " +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND (e.eventDate > :start)" +
            "     AND (:paid IS NULL OR e.paid = :paid)" +
            "     AND (:text IS NULL" +
            "         OR (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')))" +
            "         OR (UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')))" +
            "         OR (UPPER(e.title) LIKE UPPER(CONCAT('%', :text, '%'))))")
    Page<Event> findAllPublishStateAvailable(EventState state, LocalDateTime start, List<Long> categories,
                                             Boolean paid, String text, Pageable pageable);

    @Query("SELECT e" +
            " FROM Event AS e" +
            " WHERE e.state = :state" +
            "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests) " +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND (e.eventDate > :start)" +
            "     AND (:paid IS NULL OR e.paid = :paid)" +
            "     AND (:text IS NULL" +
            "          OR (UPPER(e.annotation) LIKE UPPER(:text))" +
            "          OR (UPPER(e.description) LIKE UPPER(:text)))")
    Page<Event> findAllPublishStateNotAvailable(EventState state, LocalDateTime start, List<Long> categories,
                                                Boolean paid, String text, Pageable pageable);
}