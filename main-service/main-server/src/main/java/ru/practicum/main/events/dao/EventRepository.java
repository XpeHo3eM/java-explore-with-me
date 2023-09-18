package ru.practicum.main.events.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(long id);

    List<Event> findAllByIdIn(Set<Long> events);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query(value = "SELECT e" +
            " FROM Event AS e" +
            " LEFT JOIN FETCH e.initiator AS i" +
            " LEFT JOIN FETCH e.category" +
            " WHERE i.id = :userId",
            countQuery = "SELECT e" +
                    " FROM Event AS e" +
                    " WHERE e.initiator.id = :userId")
    Page<Event> findAllWithInitiatorByInitiatorId(Long userId, Pageable pageable);

    @Query(value = "SELECT e" +
            " FROM Event AS e" +
            " JOIN FETCH e.initiator" +
            " JOIN FETCH e.category" +
            " WHERE e.eventDate > :start" +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND (e.initiator.id IN :users OR :users IS NULL)" +
            "     AND (e.state IN :states OR :states IS NULL)",
            countQuery = "SELECT e" +
                    " FROM Event AS e" +
                    " WHERE e.eventDate > :start" +
                    "     AND (e.category.id IN :categories OR :categories IS NULL)" +
                    "     AND (e.initiator.id IN :users OR :users IS NULL)" +
                    "     AND (e.state IN :states OR :states IS NULL)")
    Page<Event> findAllForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime start, Pageable pageable);

    @Query(value = "SELECT e" +
            " FROM Event e" +
            " JOIN FETCH e.initiator" +
            " JOIN FETCH e.category" +
            " WHERE e.state = :state" +
            "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)" +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND e.eventDate > :start" +
            "     AND (:paid IS NULL OR e.paid = :paid)" +
            "     AND (:text IS NULL" +
            "         OR (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')))" +
            "         OR (UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')))" +
            "         OR (UPPER(e.title) LIKE UPPER(CONCAT('%', :text, '%'))))",
            countQuery = "SELECT e" +
                    " FROM Event e" +
                    " WHERE e.state = :state" +
                    "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)" +
                    "     AND (e.category.id IN :categories OR :categories IS NULL)" +
                    "     AND e.eventDate > :start" +
                    "     AND (:paid IS NULL OR e.paid = :paid)" +
                    "     AND (:text IS NULL" +
                    "         OR (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')))" +
                    "         OR (UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')))" +
                    "         OR (UPPER(e.title) LIKE UPPER(CONCAT('%', :text, '%'))))")
    Page<Event> findAllPublishStateAvailable(EventState state, LocalDateTime start, List<Long> categories,
                                             Boolean paid, String text, Pageable pageable);

    @Query(value = "SELECT e" +
            " FROM Event AS e" +
            " JOIN FETCH e.initiator" +
            " JOIN FETCH e.category" +
            " WHERE e.state = :state" +
            "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)" +
            "     AND (e.category.id IN :categories OR :categories IS NULL)" +
            "     AND e.eventDate > :start" +
            "     AND (:paid IS NULL OR e.paid = :paid)" +
            "     AND (:text IS NULL" +
            "          OR UPPER(e.annotation) LIKE UPPER(:text)" +
            "          OR UPPER(e.description) LIKE UPPER(:text))",
            countQuery = "SELECT e" +
                    " FROM Event AS e" +
                    " WHERE e.state = :state" +
                    "     AND (e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)" +
                    "     AND (e.category.id IN :categories OR :categories IS NULL)" +
                    "     AND e.eventDate > :start" +
                    "     AND (:paid IS NULL OR e.paid = :paid)" +
                    "     AND (:text IS NULL " +
                    "          OR UPPER(e.annotation) LIKE UPPER(:text)" +
                    "          OR UPPER(e.description) LIKE UPPER(:text))")
    Page<Event> findAllPublishStateNotAvailable(EventState state, LocalDateTime start, List<Long> categories,
                                                Boolean paid, String text, Pageable pageable);
}