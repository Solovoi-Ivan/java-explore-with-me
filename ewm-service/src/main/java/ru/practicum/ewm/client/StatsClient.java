package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stats-server.url}")
    private String host;

    public void addEndpointHit(String app, String uri, String ip) {
        restTemplate.postForEntity(
                URI.create(host + "/hit"),
                new EndpointHit(app, uri, ip, LocalDateTime.now()), Object.class);
    }

    public List<ViewStats> getStats(int eventId) {
        String uriForGet = UriComponentsBuilder.fromHttpUrl(host + "/stats")
                .queryParam("start", "2000-01-01 00:00:00")
                .queryParam("end", "2100-01-01 00:00:00")
                .queryParam("uris", "/events/" + eventId)
                .encode()
                .toUriString();

        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(
                URI.create(uriForGet),
                ViewStats[].class);
        ViewStats[] array = response.getBody();
        List<ViewStats> list = new ArrayList<>();

        if (array != null) {
            list = Arrays.asList(array);
        }
        return list;
    }
}