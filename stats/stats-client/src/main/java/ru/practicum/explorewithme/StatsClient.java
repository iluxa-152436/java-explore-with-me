package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    @Value("${spring.application.name}")
    private String appName;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void postHit(String uri, String ip) {
        HitPostDto hitPostDto = HitPostDto.builder()
                .app(appName)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .uri(uri)
                .build();
        HttpEntity<HitPostDto> request = new HttpEntity<>(hitPostDto, getHttpDefaultHeaders());
        log.debug("Request in stats-client={}", request);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(statsServerUrl + "/hit",
                    request,
                    String.class);
            log.debug("Response in stats-client={}", response);
        } catch (RestClientResponseException e) {
            log.debug(e.getMessage());
        }
    }

    public List<HitGetDto> getStats(LocalDateTime start,
                                    LocalDateTime end,
                                    boolean unique,
                                    @Nullable List<String> uris) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(statsServerUrl + "/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("unique", String.valueOf(unique));
        if (uris != null) {
            builder.queryParam("uris", uris.stream().collect(Collectors.joining(",")));
        }
        HttpEntity requestEntity = new HttpEntity<>(getHttpDefaultHeaders());
        try {
            ResponseEntity<List<HitGetDto>> response = restTemplate.exchange(builder.build().toUri(),
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<HitGetDto>>() {
                    });
            if (response.getStatusCode() == HttpStatus.OK) { //обработка, только если было тело ответа
                log.debug(response.getBody().toString());
                return response.getBody();
            } else { //если ответ без тела - возвращаем пустой список
                return Collections.EMPTY_LIST;
            }
        } catch (RestClientResponseException e) {
            log.debug("Response exception in StatsClient. Return empty list. " + e.getMessage() + e.getResponseBodyAsString());
            return Collections.EMPTY_LIST;
        }
    }

    private static HttpHeaders getHttpDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
