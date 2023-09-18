package ru.practicum.main.events.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dao.CategoryRepository;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.enums.EventSort;
import ru.practicum.main.events.enums.EventState;
import ru.practicum.main.events.enums.EventStateAction;
import ru.practicum.main.events.mapper.EventMapper;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.handler.EntityNotFoundException;
import ru.practicum.main.handler.ValidateException;
import ru.practicum.main.requests.dao.RequestRepository;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.dto.ParticipationRequestMapper;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.users.dao.UserRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.dto.NewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.main.events.enums.EventSort.EVENT_DATE;
import static ru.practicum.main.events.enums.EventSort.VIEWS;
import static ru.practicum.main.events.enums.EventState.*;
import static ru.practicum.main.requests.enums.RequestStatus.CONFIRMED;
import static ru.practicum.main.requests.enums.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    @Value("${app}")
    private final String app;

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime start,
                                            LocalDateTime end,
                                            Pageable pageable) {
        Page<Event> events = eventRepository.findAllForAdmin(users, states, categories, getRangeStart(start), pageable);

        return events.stream()
                .map(mapper::toFullDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto updateByAdmin(Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEventOrThrowException(eventId);

        updateEventByAdmin(event, eventUpdatedDto);

        return mapper.toFullDto(event);
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        User user = getUserOrThrowException(userId);
        Category category = getCategoryOrThrowException(newEventDto.getCategory());

        return mapper.toFullDto(eventRepository.save(mapper.toEvent(newEventDto, user, category)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUser(Long userId, Pageable pageable) {
        return eventRepository.findAllWithInitiatorByInitiatorId(userId, pageable).stream()
                .map(mapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(EventGetAllParams eventGetAllParams) {
        String text = eventGetAllParams.getText();
        List<Long> categories = eventGetAllParams.getCategories();
        Boolean paid = eventGetAllParams.getPaid();
        LocalDateTime start = eventGetAllParams.getStart();
        LocalDateTime end = eventGetAllParams.getEnd();
        Boolean available = eventGetAllParams.getAvailable();
        EventSort sort = eventGetAllParams.getSort();
        int from = eventGetAllParams.getFrom();
        int size = eventGetAllParams.getSize();
        HttpServletRequest httpServletRequest = eventGetAllParams.getHttpServletRequest();

        validateTimeRange(start, end);

        Pageable pageable;

        if (sort.equals(EVENT_DATE)) {
            pageable = PageRequest.of(from, size, Sort.by("eventDate"));
        } else {
            pageable = PageRequest.of(from, size, Sort.unsorted());
        }

        List<Event> events;
        if (available) {
            events = eventRepository.findAllPublishStateNotAvailable(PUBLISHED, getRangeStart(start), categories,
                    paid, text, pageable).getContent();
        } else {
            events = eventRepository.findAllPublishStateAvailable(PUBLISHED, getRangeStart(start), categories,
                    paid, text, pageable).getContent();
        }

        List<EventShortDto> result = events.stream()
                .map(mapper::toShortDto)
                .collect(Collectors.toList());

        if (sort.equals(VIEWS)) {
            return result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getByIdPrivate(Long userId, Long eventId) {
        return mapper.toFullDto(getEventOrThrowException(eventId, userId));
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEventOrThrowException(eventId, userId);

        if (event.getState() == PUBLISHED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("События со статусами 'Отменен' или 'На рассмотрении' не могут быть обновлены");
        }

        updateEvent(event, eventUpdatedDto);

        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByIdPublic(Long id, HttpServletRequest request) {
        Event event = getEventOrThrowException(id);

        if (!event.getState().equals(PUBLISHED)) {
            throw new EntityNotFoundException(String.format("Событие с id = %d уже опубликовано", id));
        }

        EventFullDto fullDto = mapper.toFullDto(event);

        List<String> uris = List.of("/events/" + event.getId());
        List<HitsStatDto> views = (List<HitsStatDto>) statsClient.getStats(LocalDateTime.MIN, LocalDateTime.now(), uris, false).getBody();

        if (views != null) {
            fullDto.setViews(views.size());
        }
        statsClient.createHit(NewStatDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        return fullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getParticipationRequestPrivate(Long userId, Long eventId) {
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isPresent()) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event event = getEventOrThrowException(eventId, userId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidateException("Не удалось обновить статус");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId, statusUpdateRequest.getRequestIds());

        validateRequestStatus(requests);

        switch (statusUpdateRequest.getStatus()) {
            case CONFIRMED:
                return createConfirmedStatus(requests, event);
            case REJECTED:
                return createRejectedStatus(requests, event);
            default:
                throw new ValidateException("State is not valid: " + statusUpdateRequest.getStatus());
        }
    }

    private void validateRequestStatus(List<ParticipationRequest> requests) {
        boolean isStatusPending = requests.stream()
                .anyMatch(request -> !request.getStatus().equals(PENDING));

        if (isStatusPending) {
            throw new ValidateException("Статус не может быть изменен");
        }
    }

    private EventRequestStatusUpdateResult createConfirmedStatus(List<ParticipationRequest> requests, Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidateException("Количество участников мероприятия превысило лимит заявок на участие");
        }

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        requests.forEach(req -> {
            if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
                req.setStatus(REJECTED);
                confirmedRequests.add(req);
            } else {
                req.setStatus(CONFIRMED);
                event.setParticipantLimit(event.getParticipantLimit() + 1);
                rejectedRequests.add(req);
            }
        });


        return new EventRequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }

    private EventRequestStatusUpdateResult createRejectedStatus(List<ParticipationRequest> requests, Event event) {
        requests.forEach(request -> request.setStatus(REJECTED));
        requestRepository.saveAll(requests);
        List<ParticipationRequestDto> rejectedRequests = requests
                .stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(List.of(), rejectedRequests);
    }

    private Category getCategoryOrThrowException(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id = %d не найдена", id)));
    }

    private Event getEventOrThrowException(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие с id = %d не найдено", id)));
    }

    private Event getEventOrThrowException(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие с id = %d для пользователя " +
                        "с id = %d не найдено", eventId, userId)));
    }

    private User getUserOrThrowException(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    private void updateEvent(Event event, EventUpdatedDto eventDto) {
        updateEventCommonFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
            if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
        }
    }

    private void updateEventByAdmin(Event event, EventUpdatedDto eventDto) {
        updateEventCommonFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (!event.getState().equals(PENDING)) {
                throw new ValidateException(String.format("Некорректное состояние для публикации или отмены: %s", event.getState()));
            }

            if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                event.setState(CANCELED);
            } else if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        }

        if (eventDto.getEventDate() != null && event.getState().equals(PUBLISHED)) {
            if (!eventDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                throw new ValidateException("Дата проведения мероприятия должна быть не ранее 1 часа от даты публикации");
            }

            event.setEventDate(eventDto.getEventDate());
        }
    }

    private void updateEventCommonFields(Event event, EventUpdatedDto eventDto) {
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = getCategoryOrThrowException(eventDto.getCategory());
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

    private Long getId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Событие должно быть не ранее 2 часов от текущей даты");
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
}