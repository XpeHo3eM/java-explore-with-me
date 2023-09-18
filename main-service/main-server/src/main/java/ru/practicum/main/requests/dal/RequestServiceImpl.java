package ru.practicum.main.requests.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.dao.EventRepository;
import ru.practicum.main.handler.ValidateException;
import ru.practicum.main.handler.EntityNotFoundException;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.dto.ParticipationRequestMapper;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.requests.dao.RequestRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.dao.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.main.requests.dto.ParticipationRequestMapper.mapToNewParticipationRequest;
import static ru.practicum.main.requests.dto.ParticipationRequestMapper.mapToParticipationRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    private EventRequestStatusUpdateResult createConfirmedStatus(List<ParticipationRequest> requests, Event event) {
        validateParticipantLimit(event);
        int potentialParticipant = event.getParticipantLimit() - event.getConfirmedRequests();

        List<ParticipationRequest> confirmedRequests;
        List<ParticipationRequest> rejectedRequests;

        if (requests.size() <= potentialParticipant) {
            confirmedRequests = requests.stream()
                    .peek(request -> request.setStatus(CONFIRMED))
                    .collect(Collectors.toList());
            rejectedRequests = List.of();
        } else {
            confirmedRequests = requests.stream()
                    .limit(potentialParticipant)
                    .peek(request -> request.setStatus(CONFIRMED))
                    .collect(Collectors.toList());
            rejectedRequests = requests.stream()
                    .skip(potentialParticipant)
                    .peek(request -> request.setStatus(REJECTED))
                    .collect(Collectors.toList());
        }

        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
        eventRepository.save(event);

        List<ParticipationRequest> updatedRequests = Stream.concat(confirmedRequests.stream(), rejectedRequests.stream())
                .collect(Collectors.toList());
        requestRepository.saveAll(updatedRequests);

        List<ParticipationRequestDto> confirmedRequestsDto = confirmedRequests.stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequestsDto = rejectedRequests.stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }


    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User with id=" + userId + " hasn't found."));
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new EntityNotFoundException("Event with id=" + eventId + "  hasn't found found."));

        validateParticipantLimit(event);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidateException("You can't create same request twice");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new ValidateException("Initiator of the event can't participate in own event");
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new ValidateException("It isn't possible participate if event isn't published ");
        }

        ParticipationRequest participationRequest = requestRepository.save(mapToNewParticipationRequest(event, user));

        if (participationRequest.getStatus() == CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        log.info("Create participation request {} ", participationRequest);
        return mapToParticipationRequestDto(participationRequest);
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

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getParticipationRequestByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " hasn't found"));
        log.info("Get participation request for user with id= {}", userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto updateStatusParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id=" + requestId + " hasn't found"));
        request.setStatus(CANCELED);
        log.info("Update status participation request id= {}", requestId);
        return mapToParticipationRequestDto(requestRepository.save(request));
    }

    private void validateParticipantLimit(Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidateException("The event participant number  was reached participation request limit.");
        }
    }
}