package ru.practicum.main.events.dto;

import lombok.Value;
import ru.practicum.main.requests.enums.RequestStatus;

import java.util.Set;

@Value
public class RequestStatusUpdateRequest {
    Set<Long> requestIds;
    RequestStatus status;
}