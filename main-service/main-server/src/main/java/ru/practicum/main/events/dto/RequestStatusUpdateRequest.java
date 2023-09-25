package ru.practicum.main.events.dto;

import lombok.*;
import ru.practicum.main.requests.enums.RequestStatus;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}