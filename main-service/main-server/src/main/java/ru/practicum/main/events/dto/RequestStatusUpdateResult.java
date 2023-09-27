package ru.practicum.main.events.dto;

import lombok.Value;
import ru.practicum.main.requests.dto.RequestDto;

import java.util.List;

@Value
public class RequestStatusUpdateResult {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}