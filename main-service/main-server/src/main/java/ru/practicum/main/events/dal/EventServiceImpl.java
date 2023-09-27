package ru.practicum.main.events.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dao.CategoryRepository;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.enums.EventSort;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.mapper.EventMapper;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.handler.ConflictException;
import ru.practicum.main.handler.EntityNotFoundException;
import ru.practicum.main.handler.ValidateException;
import ru.practicum.main.requests.dao.RequestRepository;
import ru.practicum.main.requests.dto.RequestDto;
import ru.practicum.main.requests.enums.RequestStatus;
import ru.practicum.main.requests.mapper.RequestMapper;
import ru.practicum.main.requests.model.Request;
import ru.practicum.main.users.dao.UserRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitsStatDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main.events.enums.EventSort.EVENT_DATE;
import static ru.practicum.main.events.enums.EventSort.VIEWS;
import static ru.practicum.main.events.enums.EventState.*;
import static ru.practicum.main.events.enums.EventStateAction.CANCEL_REVIEW;
import static ru.practicum.main.events.enums.EventStateAction.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    @Value("${application.name}")
    private String app;

    private static <T> T getEntityOrThrowException(@NotNull JpaRepository<T, Long> storage, Long id) throws EntityNotFoundException {
        String message;
        if (storage instanceof CategoryRepository) {
            message = String.format("Категория с id = %d не найдена", id);
        } else if (storage instanceof EventRepository) {
            message = String.format("Событие с id = %d не найдено", id);
        } else if (storage instanceof UserRepository) {
            message = String.format("Пользователь с id = %d не найден", id);
        } else {
            message = "";
        }

        return storage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(message));
    }

    private static <T> void validateEntityExists(@NotNull JpaRepository<T, Long> storage, Long id) throws EntityNotFoundException {
        String message;
        if (storage instanceof EventRepository) {
            message = String.format("Событие с id = %d не найдено", id);
        } else if (storage instanceof UserRepository) {
            message = String.format("Пользователь с id = %d не найден", id);
        } else {
            message = "";
        }

        if (!storage.existsById(id)) {
            throw new EntityNotFoundException(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime start,
                                            LocalDateTime end,
                                            Pageable pageable,
                                            Boolean onlyPending) {
        validateTimeRange(start, end);

        Page<Event> events = eventRepository.findAllForAdmin(users, states, categories, getRangeStart(start), pageable);

        return events
                .stream()
                .map(eventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEntityOrThrowException(eventRepository, eventId);

        updateEventByAdmin(event, eventUpdatedDto);

        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        User user = getEntityOrThrowException(userRepository, userId);
        Category category = getEntityOrThrowException(categoryRepository, newEventDto.getCategory());

        Event event = eventMapper.toEvent(newEventDto, user, category);
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUser(Long userId, Pageable pageable) {
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(EventGetAllParams eventGetAllParams) {
        String text = eventGetAllParams.getText();
        List<Long> categories = eventGetAllParams.getCategories();
        Boolean paid = eventGetAllParams.getPaid();
        LocalDateTime start = eventGetAllParams.getStart();
        LocalDateTime end = eventGetAllParams.getEnd();
        Boolean onlyAvailable = eventGetAllParams.getAvailable();
        EventSort sort = eventGetAllParams.getSort();
        HttpServletRequest httpServletRequest = eventGetAllParams.getHttpServletRequest();
        int from = eventGetAllParams.getFrom();
        int size = eventGetAllParams.getSize();

        validateTimeRange(start, end);

        Sort sortBy = (sort.equals(EVENT_DATE)) ? Sort.by("eventDate") : Sort.unsorted();
        Pageable pageable = PageRequest.of(from / size, size, sortBy);

        List<Event> events;
        if (onlyAvailable) {
            events = eventRepository.findAllPublishStateNotAvailable(PUBLISHED, getRangeStart(start), categories,
                    paid, text, pageable).getContent();
        } else {
            events = eventRepository.findAllPublishStateAvailable(PUBLISHED, getRangeStart(start), categories,
                    paid, text, pageable).getContent();
        }

        if (end != null) {
            events = events
                    .stream()
                    .filter(event -> event.getEventDate().isBefore(end))
                    .collect(Collectors.toList());
        }

        List<EventShortDto> result = events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());

        saveViewInEvent(result);

        statsClient.postHit(httpServletRequest);

        if (sort.equals(VIEWS)) {
            return result
                    .stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdPrivate(Long userId, Long eventId) {
        return eventMapper.toFullDto(getEventOrThrowException(eventId, userId));
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEventOrThrowException(eventId, userId);

        if (event.getState() == PUBLISHED) {
            throw new ConflictException("Событие со статусом 'Опубликовано' не может быть обновлено");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Событие не может быть обновлено позднее чем за 2 часа до начала");
        }

        updateEventByUser(event, eventUpdatedDto);

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByIdPublic(Long id, HttpServletRequest httpServletRequest) {
        Event event = getEntityOrThrowException(eventRepository, id);

        if (!event.getState().equals(PUBLISHED)) {
            throw new EntityNotFoundException(String.format("Событие с id = %d уже опубликовано", id));
        }

        EventFullDto fullDto = eventMapper.toFullDto(event);
        fullDto.setViews(statsClient.getStats(event.getCreatedOn(),
                        LocalDateTime.now(),
                        List.of("/events/" + event.getId()),
                        false)
                .size());

        statsClient.postHit(httpServletRequest);

        return fullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsPrivate(Long userId, Long eventId) {
        validateEntityExists(userRepository, userId);
        validateEntityExists(eventRepository, eventId);

        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isEmpty()) {
            return Collections.emptyList();
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
                                                                     RequestStatusUpdateRequest updateRequest) {
        Event event = getEventOrThrowException(eventId, userId);

        if (event.getParticipantLimit() == 0) {
            throw new ConflictException("У события не осталось свободного места");
        }
        if (!event.getRequestModeration()) {
            throw new ConflictException("Событие не модерируется");
        }

        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, updateRequest.getRequestIds());

        validateRequestStatus(requests);

        switch (updateRequest.getStatus()) {
            case CONFIRMED:
                return createConfirmedStatus(requests, event);
            case REJECTED:
                return createRejectedStatus(requests);
            default:
                throw new ValidateException(String.format("Некорректный статус: %s", updateRequest.getStatus()));
        }
    }

    private void saveViewInEvent(List<EventShortDto> result) {
        List<String> uris = result
                .stream()
                .map(eventShortDto -> "/events/" + eventShortDto.getId())
                .collect(Collectors.toList());

        List<HitsStatDto> views = statsClient.getStats(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(),
                uris,
                null);

        if (views != null) {
            Map<Long, Long> mapIdHits = views
                    .stream()
                    .collect(Collectors.toMap(viewStats -> getId(viewStats.getUri()), HitsStatDto::getHits));

            result.forEach(eventShortDto -> {
                Long eventId = eventShortDto.getId();
                Long viewsCount = mapIdHits.getOrDefault(eventId, 0L);

                eventShortDto.setViews(viewsCount);
            });
        }
    }

    private Long getId(String url) {
        String[] uri = url.split("/");

        return Long.valueOf(uri[uri.length - 1]);
    }

    private void validateRequestStatus(List<Request> requests) {
        boolean isStatusPending = requests
                .stream()
                .anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING));

        if (isStatusPending) {
            throw new ConflictException("Статус не может быть изменен");
        }
    }

    private RequestStatusUpdateResult createConfirmedStatus(List<Request> requests, Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException("Количество участников мероприятия превысило лимит заявок на участие");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        requests.forEach(req -> {
            if (event.getParticipantLimit() - event.getConfirmedRequests() > 0) {
                req.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(req);
            } else {
                req.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(req);
            }
        });

        List<RequestDto> confirmedRequestsDto = confirmedRequests
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
        List<RequestDto> rejectedRequestsDto = rejectedRequests
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());

        return new RequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }

    private RequestStatusUpdateResult createRejectedStatus(List<Request> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));

        requestRepository.saveAll(requests);

        List<RequestDto> rejectedRequests = requests
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());

        return new RequestStatusUpdateResult(Collections.emptyList(), rejectedRequests);
    }

    private void updateEventByUser(Event event, EventUpdatedDto eventUpdatedDto) {
        if (eventUpdatedDto.getStateAction() == null) {
            return;
        }

        if (eventUpdatedDto.getStateAction().equals(CANCEL_REVIEW) && event.getState().equals(CANCELED)) {
            throw new ConflictException("Невозможно повторно отменить заявку");
        } else if (eventUpdatedDto.getStateAction().equals(SEND_TO_REVIEW) && event.getState().equals(PENDING)) {
            throw new ConflictException("Заявка уже рассматривается");
        }

        switch (eventUpdatedDto.getStateAction()) {
            case CANCEL_REVIEW:
                event.setState(CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(PENDING);
                event.setModerationComment(eventUpdatedDto.getModerationComment());
                break;
        }

        updateEventCommonFields(event, eventUpdatedDto);
    }

    private void updateEventByAdmin(Event event, EventUpdatedDto eventUpdatedDto) {
        if (eventUpdatedDto.getStateAction() != null) {
            if (!event.getState().equals(PENDING)) {
                throw new ConflictException(String.format("Некорректное состояние события для публикации или отмены: %s", event.getState()));
            }

            switch (eventUpdatedDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(CANCELED);
                    event.setModerationComment(eventUpdatedDto.getModerationComment());
                    break;
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setModerationComment(null);
                    break;
            }
        }

        if (eventUpdatedDto.getEventDate() != null && event.getState().equals(PUBLISHED)) {
            if (!eventUpdatedDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                throw new ConflictException("Дата проведения мероприятия должна быть не ранее 1 часа от даты публикации");
            }

            event.setEventDate(eventUpdatedDto.getEventDate());
        }

        updateEventCommonFields(event, eventUpdatedDto);
    }

    private void updateEventCommonFields(Event event, EventUpdatedDto eventDto) {
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = getEntityOrThrowException(categoryRepository, eventDto.getCategory());
            event.setCategory(category);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Событие должно быть не ранее 2 часов от текущей даты");
        }
    }

    private LocalDateTime getRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.now();
        }

        return rangeStart;
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }

        if (end.isBefore(start)) {
            throw new ValidateException("Начало даты не может быть позже окончания");
        }
    }

    private Event getEventOrThrowException(Long eventId, Long userId) {
        validateEntityExists(userRepository, userId);

        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие с id = %d для пользователя " +
                        "с id = %d не найдено", eventId, userId)));
    }
}