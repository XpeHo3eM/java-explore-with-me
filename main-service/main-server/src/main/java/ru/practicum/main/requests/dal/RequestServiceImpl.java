package ru.practicum.main.requests.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.handler.ConflictException;
import ru.practicum.main.handler.EntityNotFoundException;
import ru.practicum.main.requests.dao.RequestRepository;
import ru.practicum.main.requests.dto.RequestDto;
import ru.practicum.main.requests.mapper.RequestMapper;
import ru.practicum.main.requests.model.Request;
import ru.practicum.main.users.dao.UserRepository;
import ru.practicum.main.users.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.events.enums.EventState.PUBLISHED;
import static ru.practicum.main.requests.enums.RequestStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    public RequestDto create(Long eventId, Long userId) {
        User user = getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);

        validateParticipantLimit(event);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Такой запрос уже создан");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Организатор мероприятия не может создать запрос на свое мероприятие");
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Мероприятие еще не опублировано");
        }

        return mapper.toDto(requestRepository.save(createRequest(event, user)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAll(Long userId) {
        validateUserExists(userId);

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto cancel(Long requestId, Long userId) {
        Request request = getRequestOrThrowException(requestId, userId);
        request.setStatus(CANCELED);

        return mapper.toDto(request);
    }

    private User getUserOrThrowException(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    private Event getEventOrThrowException(long id) {
        return eventRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(String.format("Событие с id = %d не найдено", id)));
    }

    private Request getRequestOrThrowException(long requestId, long userId) {
        validateUserExists(userId);

        return requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ConflictException(String.format("Событие с id = %d " +
                        "для организатора с id = %d не найдено", requestId, userId)));
    }

    private void validateParticipantLimit(Event event) {
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Количество участников мероприятия превысило лимит заявок на участие");
        }
    }

    private void validateUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    private Request createRequest(Event event, User user) {
        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .event(event)
                .status(PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return request;
    }
}