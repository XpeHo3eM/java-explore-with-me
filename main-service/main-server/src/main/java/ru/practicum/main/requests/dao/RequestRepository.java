package ru.practicum.main.requests.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.requests.model.Request;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventIdAndIdIn(Long eventId, Set<Long> requestIds);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);

    List<Request> findAllByEventId(Long eventId);
}