package ru.practicum.stats.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.dto.NewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.general.util.Constants.TIME_FORMATTER;

public class StatsClient {
    private final WebClient webClient;
    private final String appName;

    public StatsClient(String serverUrl, String appName) {
        this.webClient = WebClient.create(serverUrl);
        this.appName = appName;
    }

    public void postHit(HttpServletRequest httpServletRequest) {
        NewStatDto hitDto = NewStatDto.builder()
                .app(appName)
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/hit")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(NewStatDto.class)
                .block();
    }

    public List<HitsStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(TIME_FORMATTER))
                        .queryParam("end", end.format(TIME_FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(HitsStatDto.class)
                .collectList()
                .block();
    }
}