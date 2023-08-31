package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    @Value("${spring.application.name}")
    private String appName;

    public void postHit(String uri, String ip) {
        HitPostDto hitPostDto = HitPostDto.builder()
                .app(appName)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .uri(uri)
                .build();
        HttpEntity<HitPostDto> request = new HttpEntity<>(hitPostDto, getHttpDefaultHeaders());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(statsServerUrl + "/hits",
                    request,
                    String.class);
        } catch (RestClientResponseException e) {
            log.debug(e.getMessage());
        }
    }

    public List<HitGetDto> getStats(LocalDateTime start,
                                    LocalDateTime end,
                                    boolean unique,
                                    @Nullable List<String> uris) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);
        if (uris != null) {
            parameters.put("uris", uris);
        }
        try {
            HitListDto hitListDto = restTemplate.getForObject(statsServerUrl + "/stats",
                    HitListDto.class,
                    parameters);
            return hitListDto.getHits();
        } catch (RestClientResponseException e) {
            log.debug(e.getMessage());
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
